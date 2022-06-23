package com.example.taskmanager.util;

public class ThreadUtil {

    public static void runSynchronizedTask(Thread thread){
        try {
            thread.start();
            Thread.sleep(1000);
        }catch(InterruptedException e){
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public static void runSynchronizedTask(Thread thread, int sleep){
        try {
            thread.start();
            Thread.sleep(sleep);
        }catch(InterruptedException e){
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
}
