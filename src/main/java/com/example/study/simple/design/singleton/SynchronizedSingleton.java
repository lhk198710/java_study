package com.example.study.simple.design.singleton;

public class SynchronizedSingleton {
    private static SynchronizedSingleton instance;
    private int msg;

    private SynchronizedSingleton(int msg) {
        try {
            Thread.sleep(100);
            this.msg = msg;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //synchronized 키워드 사용. 클래스 객체당 하나의 쓰레드만 접근
    public static synchronized SynchronizedSingleton getInstance(int msg) {
        if(instance == null) {
            instance = new SynchronizedSingleton(msg);
        }
        return instance;
    }

    public int getMsg() {
        return msg;
    }
}

class SynchronizedSingletonMain {
    public static int num = 1;
    public static void main(String[] args) {
        Runnable run = () -> {
            num++;
            SynchronizedSingleton singleton = SynchronizedSingleton.getInstance(num);
            System.out.println("instance : " + singleton.getMsg());
        };
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(run);
            thread.start();
        }
    }
}

