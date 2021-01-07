在本篇文章中，将继续深入分析与介绍RocketMQ消息存储部分中的关键技术—**Mmap与PageCache**、几种RocketMQ存储优化技术（包括**预先创建分配MappedFile、文件预热和mlock系统调用**）、RocketMQ内部封装类-CommitLog/MappedFile/MappedFileQueue/ConsumeQueue的简析。然后，再简要介绍下RocketMQ消息刷盘两种主要方式。在读完本篇幅后，希望读者能够对RocketMQ消息存储部分有一个更为深刻和全面的认识

### RocketMQ存储整体设计架构回顾
RocketMQ之所以能**单机支持上万的持久化队列**与其独特的存储结构是密不可分的，这里再来看下其文件存储的整体设计架构。

![image](https://mmbiz.qpic.cn/mmbiz_png/AtZHGo2bvzKJ66ic95yndHEyPjrntbUKD765tGc8En6OgzH12ojX7UyQ4SuC23AnsIto7SiaIn8aZl7PrG7PVRGQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

上面图中假设Consumer端默认设置的是同一个ConsumerGroup，因此**Consumer端线程采用的是负载订阅的方式进行消费**。从架构图中可以总结出如下几个关键点：
1. **消息生产与消息消费相互分离**

Producer端发送消息最终写入的是CommitLog（消息存储的日志数据文件），Consumer端先从ConsumeQueue（消息逻辑队列）读取持久化消息的起始物理位置偏移量offset、大小size和消息Tag的HashCode值，随后再从CommitLog中进行读取待拉取消费消息的真正实体内容部分；
2. **RocketMQ的CommitLog文件采用混合型存储**

（所有的Topic下的消息队列共用同一个CommitLog的日志数据文件），并通过建立类似索引文件—ConsumeQueue的方式来区分不同Topic下面的不同MessageQueue的消息，同时为消费消息起到一定的缓冲作用（只有ReputMessageService异步服务线程通过doDispatch异步生成了ConsumeQueue队列的元素后，Consumer端才能进行消费）。这样，只要消息写入并刷盘至CommitLog文件后，消息就不会丢失，即使ConsumeQueue中的数据丢失，也可以通过CommitLog来恢复。
3. **RocketMQ每次读写文件的时候真的是完全顺序读写么**

这里，发送消息时，**生产者端的消息确实是顺序写入CommitLog**；订阅消息时，**消费者端也是顺序读取ConsumeQueue**，然而根据其中的起始物理位置偏移量offset读取消息**真实内容却是随机读取CommitLog**。在RocketMQ集群整体的吞吐量、并发量非常高的情况下，随机读取文件带来的性能开销影响还是比较大的，那么这里如何去优化和避免这个问题呢？后面的章节将会逐步来解答这个问题。

这里，同样也可以总结下RocketMQ存储架构的优缺点：

**（1）优点：**
- ConsumeQueue消息逻辑队列较为轻量级；
- 对磁盘的访问串行化，避免磁盘竟争，不会因为队列增加导致IOWAIT增高

**2）缺点：**
- 对于CommitLog来说写入消息虽然是顺序写，但是读却变成了完全的随机读；
- Consumer端订阅消费一条消息，需要先读ConsumeQueue，再读Commit Log，一定程度上增加了开销；

### RocketMQ存储关键技术—再谈Mmap与PageCache

#### Mmap内存映射技术(零拷贝)—MappedByteBuffer的特点

Mmap内存映射和普通标准IO操作的**本质区别在于它并不需要将文件中的数据先拷贝至OS的内核IO缓冲区，而是可以直接将用户进程私有地址空间中的一块区域与文件对象建立映射关系**，这样程序就好像可以直接从内存中完成对文件读/写操作一样。只有当缺页中断发生时，直接将文件从磁盘拷贝至用户态的进程空间内，只进行了一次数据拷贝。对于容量较大的文件来说（文件大小一般需要限制在1.5~2G以下），采用Mmap的方式其读/写的效率和性能都非常高。

![image](https://mmbiz.qpic.cn/mmbiz_png/AtZHGo2bvzKJ66ic95yndHEyPjrntbUKDcScf3IGG13djUPVCMoALuKYIlo4eXpbX4uc9eibwQWjPvtZY0DUYmcQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

##### JDK NIO的MappedByteBuffer简要分析

从JDK的源码来看，MappedByteBuffer继承自ByteBuffer，其内部维护了一个逻辑地址变量—address。在建立映射关系时，MappedByteBuffer利用了JDK NIO的FileChannel类提供的map()方法把文件对象映射到虚拟内存。仔细看源码中map()方法的实现，可以发现最终其通过调用native方法map0()完成文件对象的映射工作，同时使用Util.newMappedByteBuffer()方法初始化MappedByteBuffer实例，但最终返回的是DirectByteBuffer的实例。在Java程序中使用MappedByteBuffer的get()方法来获取内存数据是最终通过DirectByteBuffer.get()方法实现（底层通过unsafe.getByte()方法，以“地址 + 偏移量”的方式获取指定映射至内存中的数据）。

##### 使用Mmap的限制
- **Mmap映射的内存空间释放的问题**：由于映射的内存空间本身就不属于JVM的堆内存区（Java Heap），因此其不受JVM GC的控制，卸载这部分内存空间需要通过系统调用 unmap()方法来实现。然而unmap()方法是FileChannelImpl类里实现的私有方法，无法直接显示调用。RocketMQ中的做法是，通过Java反射的方式调用“sun.misc”包下的Cleaner类的clean()方法来释放映射占用的内存空间；
- **MappedByteBuffer内存映射大小限制**：因为其占用的是虚拟内存（非JVM的堆内存），大小不受JVM的-Xmx参数限制，但其大小也受到OS虚拟内存大小的限制。一般来说，一次只能映射1.5~2G 的文件至用户态的虚拟内存空间，**这也是为何RocketMQ默认设置单个CommitLog日志数据文件为1G的原因了**；
- **使用MappedByteBuffe的其他问题**：会存在内存占用率较高和文件关闭不确定性的问题；


#### OS的PageCache机制
PageCache是OS对文件的缓存，**用于加速对文件的读写**。一般来说，程序对文件进行**顺序读写的速度几乎接近于内存的读写访问**，这里的主要原因就是在于OS使用PageCache机制对读写访问操作进行了性能优化，将一部分的内存用作PageCache

- **对于数据文件的读取**

如果一次读取文件时出现未命中PageCache的情况，OS从物理磁盘上访问读取文件的同时，会顺序对其他相邻块的数据文件进行预读取（ps：顺序读入紧随其后的少数几个页面）。这样，只要下次访问的文件已经被加载至PageCache时，读取操作的速度基本等于访问内存

- **对于数据文件的写入**

OS会先写入至Cache内，随后通过异步的方式由pdflush内核线程将Cache内的数据刷盘至物理磁盘上。对于文件的顺序读写操作来说，读和写的区域都在OS的PageCache内，此时读写性能接近于内存。

RocketMQ的大致做法是，**将数据文件映射到OS的虚拟内存中**（通过JDK NIO的MappedByteBuffer），写消息的时候首先写入PageCache，并通过异步刷盘的方式将消息批量的做持久化（同时也支持同步刷盘）；订阅消费消息时（对CommitLog操作是随机读取），**由于PageCache的局部性热点原理且整体情况下还是从旧到新的有序读**，因此大部分情况下消息还是可以直接从Page Cache中读取，不会产生太多的缺页（Page Fault）中断而从磁盘读取。

![image](https://mmbiz.qpic.cn/mmbiz_png/AtZHGo2bvzKJ66ic95yndHEyPjrntbUKDHdibbqyQRAFS4tHxSn68AaRNKtxHb2MHTI0olELD7iaCVpBVFMGFms6w/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

**PageCache机制也不是完全无缺点**的，当遇到OS进行脏页回写，内存回收，内存swap等情况时，就会引起较大的消息读写延迟。对于这些情况，RocketMQ采用了多种优化技术，比如内存预分配，文件预热，mlock系统调用等，来保证在**最大可能地发挥PageCache机制优点的同时，尽可能地减少其缺点带来的消息读写延迟**。

### RocketMQ存储优化技术
这一节将主要介绍RocketMQ存储层采用的几项优化技术方案在一定程度上可以减少PageCache的缺点带来的影响，主要包括**内存预分配，文件预热和mlock系统调用。**

##### 预先分配MappedFile
在消息写入过程中（调用CommitLog的putMessage()方法），CommitLog会先从MappedFileQueue队列中获取一个 MappedFile，如果没有就新建一个。这里，*MappedFile的创建过程是将构建好的一个AllocateRequest请求（具体做法是，将下一个文件的路径、下下个文件的路径、文件大小为参数封装为AllocateRequest对象）添加至队列中，后台运行的AllocateMappedFileService服务线程*（在Broker启动时，该线程就会创建并运行），会不停地run，只要请求队列里存在请求，就会去执行MappedFile映射文件的创建和预分配工作，**分配的时候有两种策略**，
- 一种是使用Mmap的方式来构建MappedFile实例，
- 另外一种是从TransientStorePool堆外内存池中获取相应的DirectByteBuffer来构建MappedFile（ps：具体采用哪种策略，也与刷盘的方式有关）。

并且，在创建分配完下个MappedFile后，**还会将下下个MappedFile预先创建并保存至请求队列中等待下次获取时直接返回**。RocketMQ中预分配MappedFile的设计非常巧妙，下次获取时候直接返回就可以不用等待MappedFile创建分配所产生的时间延迟。

![image](https://mmbiz.qpic.cn/mmbiz_png/AtZHGo2bvzKJ66ic95yndHEyPjrntbUKDLxejHicVhKaibuckSvhibIzMq4m9gljNVIm4BB8emuibicbz1g14oCyia9EA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

##### 文件预热&&mlock系统调用
**（1）mlock系统调用**

**其可以将进程使用的部分或者全部的地址空间锁定在物理内存中，防止其被交换到swap空间**。对于RocketMQ这种的高吞吐量的分布式消息队列来说，追求的是消息读写低延迟，那么肯定希望尽可能地多使用物理内存，提高数据读写访问的操作效率。

**（2）文件预热**：

预热的目的主要有两点；
- 第一点，**由于仅分配内存并进行mlock系统调用后并不会为程序完全锁定这些内存**，因为其中的分页可能是写时复制的。因此，就有必要对每个内存页面中写入一个假的值。其中，RocketMQ是在创建并分配MappedFile的过程中，预先写入一些随机值至Mmap映射出的内存空间里。
- 第二，调用Mmap进行内存映射后，OS只是建立虚拟内存地址至物理地址的映射表，而实际并没有加载任何文件至内存中。程序要访问数据时OS会检查该部分的分页是否已经在内存中，如果不在，则发出一次缺页中断。这里，可以想象下1G的CommitLog需要发生多少次缺页中断，才能使得对应的数据才能完全加载至物理内存中（ps：X86的Linux中一个标准页面大小是4KB）？RocketMQ的做法是，在做Mmap内存映射的同时进行madvise系统调用，**目的是使OS做一次内存映射后对应的文件数据尽可能多的预加载至内存中，从而达到内存预热的效果。**

### RocketMQ存储相关的模型与封装类简析
- **CommitLog**：消息主体以及元数据的存储主体，存储Producer端写入的消息主体内容。单个文件大小默认1G ，文件名长度为20位，左边补零，剩余为起始偏移量，比如00000000000000000000代表了第一个文件，起始偏移量为0，文件大小为1G=1073741824；当第一个文件写满了，第二个文件为00000000001073741824，起始偏移量为1073741824，以此类推。消息主要是顺序写入日志文件，当文件满了，写入下一个文件；

- **ConsumeQueue**：消息消费的逻辑队列，其中包含了这个MessageQueue在CommitLog中的**起始物理位置偏移量offset**，**消息实体内容的大小**和**Message Tag的哈希值**。从实际物理存储来说，ConsumeQueue对应每个Topic和QueuId下面的文件。单个文件大小约5.72M，每个文件由30W条数据组成，每个文件默认大小为600万个字节，当一个ConsumeQueue类型的文件写满了，则写入下一个文件；

- **IndexFile**：用于为生成的索引文件提供访问服务，通过消息Key值查询消息真正的实体内容。在实际的物理存储上，文件名则是以创建时的时间戳命名的，固定的单个IndexFile文件大小约为400M，一个IndexFile可以保存 2000W个索引；

- **MapedFileQueue**：对连续物理存储的抽象封装类，源码中可以通过消息存储的物理偏移量位置快速定位该offset所在MappedFile(具体物理存储位置的抽象)、创建、删除MappedFile等操作；

- **MappedFile**：文件存储的直接内存映射业务抽象封装类，源码中通过操作该类，可以把消息字节写入PageCache缓存区（commit），或者原子性地将消息持久化的刷盘（flush）；

##### RocketMQ消息刷盘的主要过程
在RocketMQ中消息刷盘主要可以分为**同步刷盘和异步刷盘**两种。

![image](https://mmbiz.qpic.cn/mmbiz_png/AtZHGo2bvzKJ66ic95yndHEyPjrntbUKDjIFp395GeiaOkxMpSickGDh29bI5iaODkml6lFg7qVSybuen6YqNeiaPlw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

**（1）同步刷盘**：如上图所示，只有在消息真正持久化至磁盘后，RocketMQ的Broker端才会真正地返回给Producer端一个成功的ACK响应。同步刷盘对MQ消息可靠性来说是一种不错的保障，但是性能上会有较大影响，一般适用于金融业务应用领域。

> **RocketMQ同步刷盘的大致做法是**，基于生产者消费者模型，主线程创建刷盘请求实例—GroupCommitRequest并在放入刷盘写队列后唤醒同步刷盘线程—GroupCommitService，来执行刷盘动作（其中用了CAS变量和CountDownLatch来保证线程间的同步）。这里，RocketMQ源码中用读写双缓存队列（requestsWrite/requestsRead）来实现读写分离，其带来的好处在于内部消费生成的同步刷盘请求可以不用加锁，提高并发度。

**（2）异步刷盘**：能够充分利用OS的PageCache的优势，只要消息写入PageCache即可将成功的ACK返回给Producer端。消息刷盘采用后台异步线程提交的方式进行，降低了读写延迟，提高了MQ的性能和吞吐量。**异步和同步刷盘的区别在于，异步刷盘时，主线程并不会阻塞，在将刷盘线程wakeup后，就会继续执行。**