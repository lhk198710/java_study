package com.example.study.simple.design.builder;

public class JacketProduction {
    public static void main(String[] args) {
        Jacket jacket = new Jacket.Builder().setNumber(1).setName("Northface").setSize(105).build();
        System.out.println(jacket);
    }
}
