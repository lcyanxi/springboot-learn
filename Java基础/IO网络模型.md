![image](http://assets.processon.com/chart_image/5ffa4ce95653bb681fa3f0c2.png)


##### 基本概念介绍

- **进程(线程)切换**

所有系统都有调度进程的能力，它可以挂起一个当前正在运行的进程，并恢复之前挂起的进程

- **进程(线程)的阻塞**

运行中的进程，有时会等待其他事件的执行完成，比如等待锁，请求I/O的读写；进程在等待过程会被系统自动执行阻塞，此时进程不占用CPU

- **文件描述符**

在Linux，文件描述符是一个用于表述指向文件引用的抽象化概念，它是一个非负整数。当程序打开一个现有文件或者创建一个进程，socket套接字时，内核都会向进程返回一个文件描述符

- **linux信号处理**

Linux进程运行中可以接受来自系统或者进程的信号值，然后根据信号值去运行相应捕捉函数；信号相当于是硬件中断的软件模拟



##### 网络IO的读写过程
- 当在用户空间发起对socket套接字的读操作时，会导致进程上下文切换，用户进程阻塞（R1）等待网络数据流到来，从网卡复制到内核；（R2）然后从内核缓冲区向用户进程缓冲区复制。此时进程切换恢复，处理拿到的数据
- 这里我们给socket读操作的第一阶段起个别名R1,第二阶段称为R2
- 当在用户空间发起对socket的写操作时（send），导致上下文切换，用户进程阻塞等待（1）数据从用户进程缓冲区复制到内核缓冲区。数据copy完成，此时进程切换恢复


##### 同步阻塞（BIO）
- **当服务端采用单线程**：当accept一个请求后，在recv或send调用阻塞时，将无法accept其他请求（必须等上一个请求处recv或send完），无法处理并发

```
while(1) {
  // accept阻塞
  client_fd = accept(listen_fd)
  fds.append(client_fd)
  for (fd in fds) {
    // recv阻塞（会影响上面的accept）
    if (recv(fd)) {} // logic
  }  
}
```
- **当服务器端采用多线程**：当accept一个请求后，开启线程进行recv，可以完成并发处理，但随着请求数增加需要增加系统线程，大量的线程占用很大的内存空间，并且线程切换会带来很大的开销，10000个线程真正发生读写事件的线程数不会超过20%，每次accept都开一个线程也是一种资源浪费

![image](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d119cd2a712a47b2b1f0d453fcf2fee1~tplv-k3u1fbpfcp-watermark.image)

- 最基础的I/O模型就是阻塞I/O模型，也是最简单的模型。所有的操作都是顺序执行的
- 阻塞IO模型中，用户空间的应用程序执行一个系统调用（recvform），会导致应用程序被阻塞，直到内核缓冲区的数据准备好，并且将数据从内核复制到用户进程。最后进程才被系统唤醒处理数据
- 在R1、R2连续两个阶段，整个进程都被阻塞


```
// 伪代码描述
while(1) {
  // accept阻塞
  client_fd = accept(listen_fd)
  // 开启线程read数据（fd增多导致线程数增多）
  new Thread func() {
    // recv阻塞（多线程不影响上面的accept）
    if (recv(fd)) {} // logic
  }  
}
```
##### 同步非阻塞（NIO）
- 服务器端当accept一个请求后，产生一个channel，将channel加入fds集合，每次轮询一遍fds集合recv(非阻塞)数据，没有数据则立即返回错误，每次轮询所有fd（包括没有发生读写事件的fd）会很浪费cpu

![image](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/04717efe7b8b4f24a46aebd4f656be9b~tplv-k3u1fbpfcp-watermark.image)

- 非阻塞IO也是一种同步IO。它是基于轮询（polling）机制实现，在这种模型中，套接字是以非阻塞的形式打开的。就是说I/O操作不会立即完成，但是I/O操作会返回一个错误代码(EWOULDBLOCK)，提示操作未完成
- 轮询检查内核数据，如果数据未准备好，则返回EWOULDBLOCK。进程再继续发起recvfrom调用，当然你可以暂停去做其他事
- 直到内核数据准备好，再拷贝数据到用户空间，然后进程拿到非错误码数据，接着进行数据处理。**需要注意，拷贝数据整个过程，进程仍然是属于阻塞的状态**
- 进程在R2阶段阻塞，虽然在R1阶段没有被阻塞，但是需要不断轮询


```
setNonblocking(listen_fd)
// 伪代码描述
while(1) {
  client_fd = accept(listen_fd) // accept非阻塞（cpu一直忙轮询）
  if (client_fd != null) {
    fds.append(client_fd)   // 有人连接
  } else {}   // 无人连接
  for (fd in fds) {
    setNonblocking(client_fd) // recv非阻塞
    if (len = recv(fd) && len > 0) { // recv 为非阻塞命令
      // 有读写数据
      // logic
    } else {} 无读写数据
  }  
}
```
##### 多路复用I/O 
服务器端采用单线程通过select/epoll等系统调用获取fd列表，遍历有事件的fd进行accept/recv/send，使其能支持更多的并发连接请求

![image](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ff9a18bdfdbc4dc695eca1be179691e1~tplv-k3u1fbpfcp-watermark.image)

- 一般后端服务都会存在大量的socket连接，如果一次能查询多个套接字的读写状态，若有任意一个准备好，那就去处理它，效率会高很多。这就是“I/O多路复用”，多路是指多个socket套接字，复用是指复用同一个进程
- linux提供了select、poll、epoll等多路复用I/O的实现方式，是现阶段主流框架常用的高性能I/O模型
- select或poll、epoll是阻塞调用
- 与阻塞IO不同，select不会等到socket数据全部到达再处理，而是有了一部分socket数据准备好就会恢复用户进程来处理。怎么知道有一部分数据在内核准备好了呢？答案：交给了系统系统处理吧
- 进程在R1、R2阶段也是阻塞；不过在R1阶段有个技巧，在多进程、多线程编程的环境下，我们可以只分配一个进程（线程）去阻塞调用select，其他线程不就可以解放了吗

```
fds = [listen_fd]
// 伪代码描述
while(1) {
  // 通过内核获取有读写事件发生的fd，只要有一个则返回，无则阻塞
  // 整个过程只在调用select、poll、epoll这些调用的时候才会阻塞，accept/recv是不会阻塞
  for (fd in select(fds)) {
    if (fd == listen_fd) {
        client_fd = accept(listen_fd)
        fds.append(client_fd)
    } elseif (len = recv(fd) && len != -1) {} // logic
  }  
```
**IO多路复用的三种实现方式:select、poll、epoll**

##### select缺点
- 单个进程所打开的FD是有限制的，通过FD_SETSIZE设置，默认1024
- 每次调用select，都需要把fd集合从用户态拷贝到内核态，这个开销在fd很多时会很大
- 对socket扫描时是线性扫描，采用轮询的方法，效率较低（高并发时）

##### poll函数接口
poll与select相比，只是没有fd的限制，其它基本一样

##### epoll函数接口
epoll只能工作在linux下，**例如：redis、nginx**

##### select/poll/epoll之间的区别

类型 | select | poll | epoll
---|---  |--- |--- 
数据结构 | bitmap | 数组 | 红黑树
最大连接数 | 1024 | 无上限 | 无上限
fd拷贝 | 每次调用select拷贝| 每次调用poll拷贝 | fd首次调用epoll_ctl拷贝，每次调用epoll_wait不拷贝
工作效率 | 轮询：O(n) | 轮询：O(n) | 回调：O(1)




##### 异步IO(AIO)
![image](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/6625a63eb7d942d4ba301949f20e9ef1~tplv-k3u1fbpfcp-watermark.image)

- 相对同步IO，异步IO在用户进程发起异步读（aio_read）系统调用之后，无论内核缓冲区数据是否准备好，都不会阻塞当前进程；在aio_read系统调用返回后进程就可以处理其他逻辑
- socket数据在内核就绪时，系统直接把数据从内核复制到用户空间，然后再使用信号通知用户进程
- R1、R2两阶段时进程都是非阻塞的

[参考文档](https://juejin.cn/post/6892687008552976398)