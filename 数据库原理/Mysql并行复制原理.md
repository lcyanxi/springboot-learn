众所周知，MySQL的复制延迟是一直被诟病的问题之一，在MySQL 5.7版本已经支持“真正”的并行复制功能，官方称为为enhanced multi-threaded slave（简称MTS），因此复制延迟问题已经得到了极大的改进。总之，**MySQL 5.7版本后，复制延迟问题永不存在**。

##### MySQL 5.6并行复制架构
从MySQL 5.6.3版本开始就支持所谓的并行复制了，**但是其并行只是基于schema的，也就是基于库的**。如果用户的MySQL数据库实例中存在多个schema，对于从机复制的速度的确可以有比较大的帮助。但在一般的MySQL使用中，一库多表比较常见，所以MySQL 5.6的并行复制对真正用户来说属于雷声大雨点小，不太合适生产使用。MySQL 5.6并行复制的架构如下所示：

![image](https://note.youdao.com/yws/res/16363/WEBRESOURCEe7c7c875358ec06ff915a139a4f303a2)

在MySQL 5.6版本之前，Slave服务器上有两个线程**I/O线程和SQL线程**。
- **I/O线程**负责接收二进制日志（更准确的说是二进制日志的event），
- **SQL线程**进行回放二进制日志。

如果在MySQL5.6版本开启并行复制功能，那么SQL线程就变为了coordinator（协调者）线程，coordinator线程主要负责以前两部分的内容：
- 若判断可以并行执行，那么选择worker线程执行事务的二进制日志。
- 若判断不可以并行执行，如该操作是DDL，亦或者是事务跨schema操作，则等待所有的worker线程执行完成之后，再执行当前的日志。

![image](https://note.youdao.com/yws/res/8381/WEBRESOURCEff6dce16836a5dc3f4946d6e6b8158d5)

这意味着coordinator线程并不是仅将日志发送给worker线程，自己也可以回放日志，但是所有可以并行的操作交付由worker线程完成。coordinator线程与worker是典型的生产者与消费者模型。

上述机制实现了基于schema的并行复制存在两个问题，
- **首先是crash safe功能不好做**，因为可能之后执行的事务由于并行复制的关系先完成执行，那么当发生crash的时候，这部分的处理逻辑是比较复杂的。从代码上看，5.6这里引入了Low-Water-Mark标记来解决该问题，从设计上看（WL#5569），其是希望借助于日志的幂等性来解决该问题，不过5.6的二进制日志回放还不能实现幂等性。
- **另一个最为关键的问题是这样设计的并行复制效果并不高**，如果用户实例仅有一个库，那么就无法实现并行回放，甚至性能会比原来的单线程更差。而单库多表是比多库多表更为常见的一种情形。

##### MySQL 5.7并行复制原理
MySQL 5.6基于库的并行复制出来后，基本无人问津，在沉寂了一段时间之后，MySQL 5.7出来了，它的并行复制以一种全新的姿态出现在了DBA面前。**MySQL 5.7才可称为真正的并行复制**，这其中最为主要的**原因就是slave服务器的回放与master是一致的，即master服务器上是怎么并行执行的，那么slave上就怎样进行并行回放**。不再有库的并行复制限制，对于二进制日志格式也无特殊的要求（基于库的并行复制也没有要求）。

从MySQL官方来看，其并行复制的**原本计划是支持表级的并行复制和行级的并行复制**，行级的并行复制通过解析ROW格式的二进制日志的方式来完成，WL#4648。但是最终出现给小伙伴的确是在开发计划中称为：MTS（Prepared transactions slave parallel applier），可见：WL#6314。该并行复制的思想最早是由MariaDB的Kristain提出，并已在MariaDB 10中出现，相信很多选择MariaDB的小伙伴最为看重的功能之一就是并行复制。

> 下面来看看MySQL 5.7中的并行复制究竟是如何实现的？

**组提交（group commit）**：通过对事务进行分组，优化减少了生成二进制日志所需的操作数。当事务同时提交时，它们将在单个操作中写入到二进制日志中。**如果事务能同时提交成功，那么它们就不会共享任何锁，这意味着它们没有冲突，因此可以在Slave上并行执行**。所以通过在主机上的二进制日志中添加组提交信息，这些Slave可以并行地安全地运行事务

首先，MySQL 5.7的并行复制基于一个前提，即所有已**经处于prepare阶段的事务**，都是可以并行提交的。这些当然也可以在从库中并行提交，因为处理这个阶段的事务，都是没有冲突的，该获取的资源都已经获取了。反过来说，如果有冲突，则后来的会等已经获取资源的事务完成之后才能继续，故而不会进入prepare阶段。这是一种新的并行复制思路，完全摆脱了原来一直致力于为了防止冲突而做的分发算法，等待策略等复杂的而又效率底下的工作。

MySQL5.7并行复制的思想一言以蔽之：**一个组提交（group commit）的事务都是可以并行回放，因为这些事务都已进入到事务的prepare阶段，则说明事务之间没有任何冲突**（否则就不可能提交）。

根据以上描述，这里的重点是如何来定义哪些事务是处于prepare阶段的？以及在生成的Binlog内容中该如何告诉Slave哪些事务是可以并行复制的？为了兼容MySQL 5.6基于库的并行复制，5.7引入了新的变量slave-parallel-type，其可以配置的值有：DATABASE（默认值，基于库的并行复制方式）、LOGICAL_CLOCK（基于组提交的并行复制方式）

##### 支持并行复制的GTID
**那么如何知道事务是否在同一组中**，又是一个问题，因为原版的MySQL并没有提供这样的信息。在MySQL 5.7版本中，其设计方式是将组提交的信息存放在GTID中。那么如果用户没有开启GTID功能，即将参数gtid_mode设置为OFF呢？故MySQL 5.7又引入了称之为Anonymous_Gtid（ANONYMOUS_GTID_LOG_EVENT）的二进制日志event类型

```
mysql> SHOW BINLOG EVENTS in 'mysql-bin.000006';

+------------------+-----+----------------+-----------+-------------+-----------------------------------------------+

| Log_name         | Pos | Event_type     | Server_id | End_log_pos | Info                                          |

+------------------+-----+----------------+-----------+-------------+-----------------------------------------------+

| mysql-bin.000006 | 4   | Format_desc    | 88        | 123         | Server ver: 5.7.7-rc-debug-log, Binlog ver: 4 |

| mysql-bin.000006 | 123 | Previous_gtids | 88        | 194         |                                               |

| mysql-bin.000006 | 194 | Anonymous_Gtid | 88        | 259         | SET @@SESSION.GTID_NEXT= 'ANONYMOUS'          |

| mysql-bin.000006 | 259 | Query          | 88        | 330         | BEGIN                                         |

| mysql-bin.000006 | 330 | Table_map      | 88        | 373         | table_id: 108 (aaa.t)                         |

| mysql-bin.000006 | 373 | Write_rows     | 88        | 413         | table_id: 108 flags: STMT_END_F               |

......
```

当开启GTID时，每一个操作语句（DML/DDL）执行前就会添加一个GTID事件，记录当前全局事务ID；同时在MySQL 5.7版本中，组提交信息也存放在GTID事件中，**有两个关键字段last_committed，sequence_number就是用来标识组提交信息的**。在InnoDB中有一个全局计数器（global counter），在每一次存储引擎提交之前，计数器值就会增加。在事务进入prepare阶段之前，全局计数器的当前值会被储存在事务中，这个值称为此事务的commit-parent（也就是last_committed）。

这意味着在MySQL5.7版本中即使不开启GTID，每个事务开始前也是会存在一个Anonymous_Gtid，而这个Anonymous_Gtid事件中就存在着组提交的信息。反之，如果开启了GTID后，就不会存在这个Anonymous_Gtid了，从而组提交信息就记录在非匿名GTID事件中。

##### LOGICAL_CLOCK
然而，通过上述的SHOW BINLOG EVENTS，我们并没有发现有关组提交的任何信息。但是通过mysqlbinlog工具，用户就能发现组提交的内部信息：

```
$ mysqlbinlog mysql-bin.0000006 | grep last_committed

#150520 14:23:11 server id 88 end_log_pos 259   CRC32 0x4ead9ad6 GTID last_committed=0  sequence_number=1

#150520 14:23:11 server id 88 end_log_pos 1483  CRC32 0xdf94bc85 GTID last_committed=0  sequence_number=2

#150520 14:23:11 server id 88 end_log_pos 2708  CRC32 0x0914697b GTID last_committed=0  sequence_number=3

#150520 14:23:11 server id 88 end_log_pos 3934  CRC32 0xd9cb4a43 GTID last_committed=0  sequence_number=4

#150520 14:23:11 server id 88 end_log_pos 5159  CRC32 0x06a6f531 GTID last_committed=0  sequence_number=5

#150520 14:23:11 server id 88 end_log_pos 6386  CRC32 0xd6cae930 GTID last_committed=0  sequence_number=6

#150520 14:23:11 server id 88 end_log_pos 7610  CRC32 0xa1ea531c GTID last_committed=6  sequence_number=7

#150520 14:23:11 server id 88 end_log_pos 8834  CRC32 0x96864e6b GTID last_committed=6  sequence_number=8

#150520 14:23:11 server id 88 end_log_pos 10057 CRC32 0x2de1ae55 GTID last_committed=6  sequence_number=9

#150520 14:23:11 server id 88 end_log_pos 11280 CRC32 0x5eb13091 GTID last_committed=6  sequence_number=10

#150520 14:23:11 server id 88 end_log_pos 12504 CRC32 0x16721011 GTID last_committed=6  sequence_number=11

#150520 14:23:11 server id 88 end_log_pos 13727 CRC32 0xe2210ab6 GTID last_committed=6  sequence_number=12

#150520 14:23:11 server id 88 end_log_pos 14952 CRC32 0xf41181d3 GTID last_committed=12 sequence_number=13

...
```
可以发现MySQL5.7二进制日志较之原来的二进制日志内容多了last_committed和sequence_number。
- **last_committed**：表示事务提交的时候，上次事务提交的编号，如果事务具有相同的last_committed，表示这些事务都在一组内，可以进行并行的回放。例如上述last_committed为0的事务有6个，表示组提交时提交了6个事务，而这6个事务在从机是可以进行并行回放的。
- **sequence_number**是顺序增长的，每个事务对应一个序列号。

另外，还有一个细节，其实每一个组的last_committed值，都是上一个组中事务的sequence_number最大值，也是本组中事务sequence_number最小值减1。同时这两个值的有效作用域都在文件内，只要换一个文件（flush binary logs），这两个值就都会从0开始计数。**上述的last_committed和sequence_number代表的就是所谓的LOGICAL_CLOCK**。

那么此时，还有一个重要的技术问题–**MySQL是如何做到将这些事务分组的呢**？要搞清楚这个问题，首先需要了解一下MySQL事务提交方式

### 事务两阶段提交

事务的提交主要分为两个主要步骤：

1. **准备阶段**（Prepare Phase）：此时SQL已经成功执行，并生成xid信息及redo和undo的内存日志。然后调用prepare方法完成第一阶段，papare方法实际上什么也没做，将事务状态设为TRX_PREPARED，并将redo log刷磁盘。

2. **提交阶段**(Commit Phase)

- 2.1 记录协调者日志，即Binlog日志：如果事务涉及的所有存储引擎的prepare都执行成功，则调用TC_LOG_BINLOG::log_xid方法将SQL语句写到binlog（write()将binary log内存日志数据写入文件系统缓存，fsync()将binary log文件系统缓存日志数据永久写入磁盘）。此时，事务已经铁定要提交了。否则，调用ha_rollback_trans方法回滚事务，而SQL语句实际上也不会写到binlog。

- 2.2 告诉引擎做commit：最后，调用引擎的commit完成事务的提交。会清除undo信息，刷redo日志，将事务设为TRX_NOT_STARTED状态。

##### ordered commit

关于MySQL是如何提交的，内部使用ordered_commit函数来处理的。先看它的逻辑图，如下：

![image](https://note.youdao.com/yws/res/16431/WEBRESOURCE1b3497792216103551cbb0321efb3b66)

从图中可以看到，只要事务提交（调用ordered_commit），就都会先加入队列中。而提交有三个步骤，包括FLUSH、SYNC及COMMIT，相应地也有三个队列。首先要加入的是FLUSH队列，如果某个事务加入时，队列还是空的，则这个事务就担任队长，来代表其他事务执行提交操作。而在其他事务继续加入时，就会发现此时队列已经不为空了，那么这些事务就会等待队长帮它们完成提交操作。在上图中，事务2-6都是这种坐享其成之辈，事务1就是队长了。不过这里需要注意一点，不是说队长会一直等待要提交的事务不停地加入，而是有一个时限，只有在这个时限之内成功加入到队列的，才能帮它提交。这个时限就是从队长加入开始，到它去处理队列的时间，这个时间实际非常小，基本上就是程序从这行到哪行的一个过程，也没有刻意去等待。

只要对长将这个队列中的事务取出，其他事务就可以加入这个队列了。第一个加入的还是队长，但此时必须要等待。因为此时有事务正在做FLUSH，做完FLUSH之后，其他的对长才能带着队员做FLUSH。而在同一时刻，只能有一个组在做FLUSH。这就是上图中所示的等待事务组2和等待事务组3，此时队长会按照顺序依次做FLUSH，做FLUSH的过程中，有一些重要的事务需要去做，如下：

1. 要保证顺序必须是提交加入到队列的顺序。
2. 如果有新的事务提交，此时队列为空，则可以加入到FLUSH队列中。不过，因为此时FLUSH临界区正在被占用，所以新事务组必须要等待。
3. 给每个事务分配sequence_number，如果是第一个事务，则将这个组的last_committed设置为sequence_number-1.
4. 将带着last_committed与sequence_number的GTID事件FLUSH到Binlog文件中。
5. 将当前事务所产生的Binlog内容FLUSH到Binlog文件中。


这样，一个事务的FLUSH就完成了。接下来，依次做完组内所有事务的FLUSH，然后做SYNC。如果SYNC的临界区是空的，则直接做SYNC操作，而如果已经有事务组在做，则必须要等待。同样地，做完FLUSH之后，FLUSH临界区会空闲出来，哪儿此时再等待这个临界区的组就可以做FLUSH操作了。总而言之，每个步骤都会有事务组在做， 就像一个流水线一样。完成一件产品需要三个工序，每个工序都可以批量来做，那么每个工序车间都不会闲着，都一直重复着相同的事情，最终每个产品都是以完全相同的顺序完成。

到COMMIT时，实际做的是存储引擎提交，参数binlog_order_commits会影响提交行为。如果设置为ON，那么此时提交就变为串行操作了，就以队列的顺序为提交顺序。而如果设置为OFF，提交就不会在这里进行，而会在每个事务（包括队长和队员）做finish_commit（FINISH）时各自做存储引擎的提交操作。组内每个事务做finish_commit是在队长完成COMMIT工序之后进行，到步骤DONE时，便会唤醒每个等待提交完成的事务，告诉他们可以继续了，那么每个事务就会去做finish_commit。而后，自己再去做finish_commit。这样，一个组的事务就都按部就班地提交完成了。现在也可以知道，与这个组中同时在做提交的最多还有另外两个事务，一个是在做FLUSH，一个是在做SYNC。

现在应该搞明白关于order commit的原理了，而这也是LOGICAL_CLOCK并行复制的基础。因为order commit使得所有的事务分了组，并且有了序列号，从库拿到这些信息之后，就可以根据序号放心大胆地做分发了。

但是有没有发现一个问题，每个组的事务数都没有做过特殊处理。因为从时间上说，从队长开始入队，到取队列中的所有事务出来，这之间的时间是非常非常小的，其实就是几行代码的事，也不会有任何费时间的操作，所以在这段时间内其实不会有多少个事务。只有在压力很大，提交的事务非常多的时候，才会提高并发度（组内事务数变大）。不过这个问题也可以解释得通，主库压力小的时候，从库何必要那么大的并发度呢？只有主库压力大的时候，从库才会延迟。

这种情况下也可以通过调整**主服务器上的参数binlog_group_commit_sync_delay、binlog_group_commit_sync_no_delay_count**。
- 前者表示事务延迟提交多少时间来加大整个组提交的事务数量，从而减少进行磁盘刷盘sync的次数，单位为1/1000000秒，最大值1000000也就是1秒；
- 后者表示组提交的事务数量凑齐多少此值时就跳出等待，然后提交事务，而无需等待binlog_group_commit_sync_delay的延迟时间；但是binlog_group_commit_sync_no_delay_count也不会超过binlog_group_commit_sync_delay设置。

几个参数都是为了增加主服务器组提交的事务比例，从而增大从机MTS的并行度。

##### 从库多线程复制分发原理
知道了order commit原理之后，现在很容易可以想到在从库端是如何分发的，从库以事务为单位做APPLY的，每个事务有一个GTID事件，从而都有一个last_committed及sequence_number值，分发原理如下。
1. 从库SQL线程拿到一个新事务，取出last_committed及sequence_number值。
2. 判断当前last_committed是不是大于当前已经执行的sequence_number的最小值（low water mark，下面称lwm）。
3. 如果大于，则说明上一个组的事务还没有完成。此时等待lwm变大，直到last_committed与lwm相等，才可以继续。
4. 如果小于或等于，则说明当前事务与正在执行的组是同一个组，不需要等待。
5. SQL线程通过统计，找到一个空闲的worker线程，如果没有空闲，则SQL线程转入等待状态，直到找到一个为止。
6. 将当前事务打包，交给选定的worker，之后worker线程会去APPLY这个事务，此时的SQL线程就会处理下一个事务。

> 说明：上面的步骤是以事务为单位介绍的，其实实际处理中还是一个事件一个事件地分发。如果一个事务已经选定了worker，而新的event还在那个事务中，则直接交给那个worker处理即可。

[原文地址](https://blog.csdn.net/andong154564667/article/details/82117727)