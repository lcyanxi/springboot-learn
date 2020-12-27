# ExecutorCompletionService深入解析
一批任务使用线程池处理，并且需要获得结果，但是不关心任务执行结束后输出结果的先后顺序，应该如何实现？

ExecutorCompletionService解决了：
1. ExecutorService的submit的Future存储问题
2. 遍历Future的list时，get()方法时阻塞的，就算使用get(long timeout, TimeUnit unit)方法，避免不了需要通过while来循环获取结果

##### 模拟future.get()会阻塞

```
public class ThreadPoolExecutorDemo {
    private static final String SUCCESS = "success";

    public static void main(String[] args) {
        // 构造一个线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 6, 3,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3));

        List<Future<String>> objects = Lists.newArrayList();

        System.out.println("------------------任务开始执行---------------------");
        for (int i = 0; i < 5; i++){
            Future<String> future = threadPool.submit(() -> {
                try {
                    String name = Thread.currentThread().getName();
                    long time = (long) (Math.random()*10);
                    System.out.println(name + "开始睡眠" + time +"ms");
                    TimeUnit.SECONDS.sleep(time);
                    System.out.println(name + "submit方法执行任务完成");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return SUCCESS;
            });
            objects.add(future);
        }

        if (!CollectionUtils.isEmpty(objects)){
            objects.forEach(future -> {
                String s = null;
                try {
                    s = future.get();
                    System.out.println("获取到数据" + s);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }

        System.out.println("-------------------main thread end---------------------");
    }
}
```
结果：

```
------------------任务开始执行---------------------
pool-1-thread-5开始睡眠6ms
pool-1-thread-3开始睡眠6ms
pool-1-thread-1开始睡眠4ms
pool-1-thread-4开始睡眠3ms
pool-1-thread-2开始睡眠7ms
pool-1-thread-4submit方法执行任务完成
pool-1-thread-1submit方法执行任务完成
获取到数据success
pool-1-thread-5submit方法执行任务完成
pool-1-thread-3submit方法执行任务完成
pool-1-thread-2submit方法执行任务完成
获取到数据success
获取到数据success
获取到数据success
获取到数据success
-------------------main thread end---------------------
```

##### ExecutorService和CompletionService区别：
**ExecutorService**：自己维护一个list保存submit的callable task所返回的Future对象。在主线程中遍历这个list并调用Future的get()方法取到Task的返回值。

**CompletionService**：在很多地方会看到一些代码通过CompletionService包装ExecutorService，然后调用其take()方法去取Future对象。

**这两者最主要的区别**:  
- 在于submit的task不一定是按照加入自己维护的list顺序完成的。从list中遍历的每个Future对象并不一定处于完成状态，这时调用get()方法就会被阻塞住，如果系统是设计成每个线程完成后就能根据其结果继续做后面的事，这样对于处于list后面的但是先完成的线程就会增加了额外的等待时间。
- 而CompletionService的实现是维护一个保存Future对象的BlockingQueue。只有当这个Future对象状态是结束的时候，才会加入到这个Queue中，take()方法其实就是Producer-Consumer中的Consumer。它会从Queue中取出Future对象，如果Queue是空的，就会阻塞在那里，直到有完成的Future对象加入到Queue中。
所以，先完成的必定先被取出。这样就减少了不必要的等待时间。

ExecutorCompletionService将最先执行结束的任务先返回，这样的好处就是不用为了等待前面的任务，导致后续的阻塞。
样例：

```
//启用多线程
CompletionService<Map<Integer,List<Integer>>> completionService = new ExecutorCompletionService<>(threadPoolTaskExecutor);
for (Integer integer : userIds) {
    completionService.submit(() -> {
        Map<Integer,List<Integer>> resultMap=Maps.newHashMap();
        try {
            do................
        } catch (Exception e) {
            resultMap.put(integer,liveIds);
        }
        return resultMap;
    });
}
Map<Integer,List<Integer>>  tempMap= Maps.newHashMap();
//等待线程池执行完
for (int i = 0; i < userIds.size(); i++) {
    try {
        Future<Map<Integer,List<Integer>>> future = completionService.poll(5000, TimeUnit.MILLISECONDS);
        if (future != null) {
            Map<Integer,List<Integer>> temp = future.get();
            if (MapUtils.isNotEmpty(temp)) {
                tempMap.putAll(temp);
            }
        }
    } catch (Exception e) {}
}
// 回收资源
threadPoolTaskExecutor.shutdown();
```
ExecutorCompletionService的构造方法，
```
public ExecutorCompletionService(Executor executor,
                                 BlockingQueue<Future<V>> completionQueue) {
    if (executor == null || completionQueue == null)
        throw new NullPointerException();
    this.executor = executor;
    this.aes = (executor instanceof AbstractExecutorService) ?
        (AbstractExecutorService) executor : null;
    this.completionQueue = completionQueue;
}
```
ExecutorCompletionService 的submit方法，最终调用调用ThreadPoolExecutor的execute方法

```
public Future<V> submit(Callable<V> task) {
    if (task == null) throw new NullPointerException();
    RunnableFuture<V> f = newTaskFor(task);
    executor.execute(new QueueingFuture(f));
    return f;
}

private RunnableFuture<V> newTaskFor(Callable<V> task) {
    if (aes == null)
        return new FutureTask<V>(task);
    else
        return aes.newTaskFor(task);
}

// 调用ThreadPoolExecutor的execute方法
public void execute(Runnable command) {
    if (command == null)
        throw new NullPointerException();

    int c = ctl.get();
    if (workerCountOf(c) < corePoolSize) {
        if (addWorker(command, true))
            return;
        c = ctl.get();
    }
    if (isRunning(c) && workQueue.offer(command)) {
        int recheck = ctl.get();
        if (! isRunning(recheck) && remove(command))
            reject(command);
        else if (workerCountOf(recheck) == 0)
            addWorker(null, false);
    }
    else if (!addWorker(command, false))
        reject(command);
}
```

# 自己设计一个线程池
1. 一个存放所有线程的集合 可以用ArryList
2. 每有一个任务分配给线程池，我们就从线程池中分配一个线程处理它。但当线程池中的线程都在运行状态，没有空闲线程时，我们还需要一个队列来存储提交给线程池的任务 需要一个队列 ArrayBlockingQueue<Runnable>
3. 初始化一个线程池时，要指定这个线程池的大小 threadNum
4. 还需要一个变量来保存已经运行的线程数目 workThreadNum

从任务队列中取出任务，分配给线程池中“空闲”的线程完成？怎么实现？

解决思路：线程池中的所有线程一直都是运行状态的，线程的空闲只是代表此刻它没有在执行任务而已；我们可以让运行中的线程，一旦没有执行任务时，就自己从队列中取任务来执行


```
public class MyThreadPool {

    /**
     * 存放线程的集合
     */
    private ArrayList<MyThead> threads;
    /**
     * 任务队列
     */
    private ArrayBlockingQueue<Runnable> taskQueue;

    /**
     * 线程池初始限定大小
     */
    private int threadNum;

    /**
     * 已经工作的线程数目
     */
    private int workThreadNum;

    private final ReentrantLock mainLock = new ReentrantLock();

    public MyThreadPool(int initPoolNum) {
        threadNum = initPoolNum;
        threads = new ArrayList<>(initPoolNum);
        //任务队列初始化为线程池线程数的四倍
        taskQueue = new ArrayBlockingQueue<>(initPoolNum * 4);
        threadNum = initPoolNum;
        workThreadNum = 0;
    }

    public void execute(Runnable runnable) {
        try {
            mainLock.lock();
            //线程池未满，每加入一个任务则开启一个线程
            if(workThreadNum < threadNum) {
                MyThead myThead = new MyThead(runnable);
                myThead.start();
                threads.add(myThead);
                workThreadNum++;
            }
            //线程池已满，放入任务队列，等待有空闲线程时执行
            else {
                //队列已满，无法添加时，拒绝任务
                if(!taskQueue.offer(runnable)) {
                    rejectTask();
                }
            }
        } finally {
            mainLock.unlock();
        }
    }

    private void rejectTask() {
        System.out.println("任务队列已满，无法继续添加，请扩大您的初始化线程池！");
    }


    class MyThead extends Thread{
        private Runnable task;
        public MyThead(Runnable runnable) {
            this.task = runnable;
        }
        @Override
        public void run() {
            //该线程一直启动着，不断从任务队列取出任务执行
            while (true) {
                //如果初始化任务不为空，则执行初始化任务
                if(task != null) {
                    task.run();
                    task = null;
                }
                //否则去任务队列取任务并执行
                else {
                    Runnable queueTask = taskQueue.poll();
                    if(queueTask != null)
                        queueTask.run();
                }
            }
        }
    }
```