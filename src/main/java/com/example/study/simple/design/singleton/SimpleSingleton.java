package com.example.study.simple.design.singleton;

public class SimpleSingleton {
    private static SimpleSingleton instance;
    private int msg;

    //외부에서 생성자 호출 막기
    private SimpleSingleton(int msg) {
        this.msg = msg;
    }

    //인스턴스를 전달
    public static SimpleSingleton getInstance(int msg) {
        if (instance == null) {
            instance = new SimpleSingleton(msg);
        }
        return instance;
    }

    public void printMsg() {
        System.out.println(msg);
    }
}

class SimpleSingletonMain {
    public static void main(String[] args) {
        SimpleSingleton instance = SimpleSingleton.getInstance(1);
        SimpleSingleton instance2 = SimpleSingleton.getInstance(2);
        instance.printMsg();
        instance2.printMsg();
    }
}
