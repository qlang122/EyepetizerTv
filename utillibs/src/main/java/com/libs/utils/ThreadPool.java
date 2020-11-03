package com.libs.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static ExecutorService mPool;
    public static final int THREADNUMS = 5;

    public static ExecutorService getExecutor() {
        synchronized (ThreadPool.class) {
            if (mPool == null) newPool();
        }
        return mPool;
    }

    public static void newPool() {
        if (mPool == null || mPool.isShutdown() || mPool.isTerminated()) {
            mPool = Executors.newFixedThreadPool(THREADNUMS);
        }
    }

    public static void execute(Runnable runnable) {
        if (mPool == null) newPool();
        mPool.execute(runnable);
    }

    public static void shutDown() {
        mPool = null;
    }
}
