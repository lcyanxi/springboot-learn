MySQL在主从复制的过程中，数据的同步是通过binlog进行的，

简单理解就是主服务器把数据变更记录到binlog中，然后再把binlog同步传输给从服务器，从服务器接收到binlog之后，再把其中的数据恢复到自己的数据库存储中。

那么，binlog里面记录的是什么内容呢？格式是怎样的呢？MySQL的binlog主要支持三种格式，分别是statement、row以及mixed。MySQL是在5.1.5版本开始支持row的、在5.1.8版本中开始支持mixed。

statement和row最大的区别，当binlog的格式为statement时，binlog里面记录的就是SQL语句的原文

因为MySQL早期只有statement这种binlog格式，这时候，如果使用提交读（ReadCommitted)、未提交读（ReadUncommitted）这两种隔离级别会出现问题。
![image](https://note.youdao.com/yws/res/19945/WEBRESOURCEde9b9ac373ec36fe76b546d6b1a60c9b)
![image](https://note.youdao.com/yws/res/19943/WEBRESOURCE390ea1085d5c8fe06448cdc478cbf20f)

> 即使Session1的删除操作在Session2的插入操作之后提交，由于READ COMMITTED的隔离级别，Session2的插入操作不会看到Session1的删除操作，所以最后数据库中仍然会留下Session2插入的记录(10,99)。

以上两个事务执行之后，会在bin log中记录两条记录，因为事务2先提交，所以insert into t1values（10,99)；会被优先记录，然后再记录delete from t1where b<100；

这样binlog同步到备库之后，SQL语句回放时，会先执行insertintot1values（10，99）；，再执行delete from t1 where b < 100;

这时候，数据库中的数据就会变成EMPTYSET，即没有任何数据。这就导致主库和备库的数据不一致了！！

为了避免这样的问题发生。MySQL就把数据库的默认隔离级别设置成了RepetableRead，那么，RepetableRead的隔离级别下是如何解决这样问题的那？那是因为RepetableRead这种隔离级别，会在更新数据的时候不仅对更新的行加行级锁，还会增加GAP锁和临键锁。上面的例子，在事务2执行的时候，因为事务1增加了GAP锁和临键锁，就会导致事务2执行被卡住，需要等事务1提交或者回滚后才能继续执行。

**为什么MySQL选择RR作为默认的数据库隔离级别**?

其实就是为了兼容历史上的那种statement格式的binlog。