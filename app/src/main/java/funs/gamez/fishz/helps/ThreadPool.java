package funs.gamez.fishz.helps;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池
 */
public class ThreadPool {
    private static ExecutorService pool = null;

    /*初始化线程池*/
    public static void init() {
        if (pool == null) {
            pool = Executors.newCachedThreadPool();
        }
    }

    /*提交任务执行*/
    public static void execute(Runnable r) {
        init();
        pool.execute(r);
    }

    /* 关闭线程池*/
    public static void unInit() {
        if (pool == null || pool.isShutdown()) return;
        pool.shutdownNow();
        pool = null;
    }
}
