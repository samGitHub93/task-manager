package com.example.taskmanager.util;

import android.util.Log;

public class ThreadUtil {
    public static void runSynchronizedTask(Thread thread){
        try {
            thread.start();
            Thread.sleep(1000);
        }catch(InterruptedException e){
            Log.e(ThreadUtil.class.getName(), e.getMessage(), e);
        }
    }

    public static void runNotSynchronizedTask(Thread thread){
        try{
            synchronized (ThreadUtil.class){
                thread.start();
                thread.join();
                Thread.sleep(1000);
            }
        }catch(InterruptedException e){
            Log.e(ThreadUtil.class.getName(), e.getMessage(), e);
        }
    }

    public static void runSynchronizedTask(Thread thread, int sleep){
        try {
            thread.start();
            Thread.sleep(sleep);
        }catch(InterruptedException e){
            Log.e(ThreadUtil.class.getName(), e.getMessage(), e);
        }
    }

    public static void runNotSynchronizedTask(Thread thread, int sleep){
        try{
            synchronized (ThreadUtil.class){
                thread.start();
                thread.join();
                Thread.sleep(sleep);
            }
        }catch(InterruptedException e){
            Log.e(ThreadUtil.class.getName(), e.getMessage(), e);
        }
    }
}
