package com.example.study.simple.design.proxy;

public class Proxy implements Subject{
    private final RealSubject realSubject = new RealSubject();

    @Override
    public String request() {
        return realSubject.request();
    }
}
