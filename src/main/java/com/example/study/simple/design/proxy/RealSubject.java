package com.example.study.simple.design.proxy;

public class RealSubject implements Subject {
    @Override
    public String request() {
        return "HelloWorld";
    }
}
