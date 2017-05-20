package com.lvsijian8.flowerpot.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**线程管理者
 * Created by Administrator on 2017/1/17.
 */
public class ThreadManager {
    private static ThreadPool pool;
    //只允许一个pool
    public static ThreadPool getThreadPool(){
        if (pool==null){
            synchronized (ThreadManager.class){
                if (pool==null){
                    //int cpuCount=Runtime.getRuntime().availableProcessors();//获取cpu核数；
                    //int threadCount=cpuCount*2+1;//线程个数
                    int threadCount=10;
                    pool=new ThreadPool(threadCount,threadCount,1L);
                }
            }
        }
        return pool;
    }

    //线程池
    public static class ThreadPool{
        private ThreadPoolExecutor executor;
        private int corePoolSize;//核心线程数
        private int maximunPoolSize;//最大线程数
        private long keepyAliveTime;//线程的休眠时间
        //TimeUnit.SECONDS 秒的表示法
        //new LinkedBlockingQueue<Runnable>() 线程队列
        //Executors.defaultThreadFactory() 默认的线程工厂
        //new ThreadPoolExecutor.AbortPolicy() 线程异常处理策略
        private ThreadPool(int corePoolSize,int maximunPoolSize,long keepyAliveTime){
            this.corePoolSize=corePoolSize;
            this.maximunPoolSize=maximunPoolSize;
            this.keepyAliveTime=keepyAliveTime;
        }



        public void execute(Runnable r){
           if (executor==null){
               executor=new ThreadPoolExecutor(corePoolSize,maximunPoolSize,keepyAliveTime,
                       TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(),
                       Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
           }
            executor.execute(r);//执行Runnable
        }
        public void cancle(Runnable r){
            if(executor!=null){
                executor.getQueue().remove(r);//从线程队列中移除该对象
            }
        }
    }
}
