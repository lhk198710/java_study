package com.example.study.simple.design.singleton;

public class EagerSingleton {
    // 클래스 로더가 초기화하는 시점에 인스턴스를 메모리에 등록하는 방법이다.
    private static EagerSingleton instance = new EagerSingleton(0);
    private int msg;

    private EagerSingleton(int msg) {
        try {
            Thread.sleep(100);
            this.msg = msg;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static EagerSingleton getInstance() {
        return instance;
    }
    public int getMsg() {
        return msg;
    }
}

class EagerSingletonMain {
    public static int num = 1;
    public static void main(String[] args) {
        Runnable run = () -> {
            num++;
            EagerSingleton singleton = EagerSingleton.getInstance();
            System.out.println("instance : " + singleton.getMsg());
        };
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(run);
            thread.start();
        }
    }
}
