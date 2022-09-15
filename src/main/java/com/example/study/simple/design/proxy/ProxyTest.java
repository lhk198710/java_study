package com.example.study.simple.design.proxy;

public class ProxyTest {
    public static void main(String[] args) {
        /**
         * 프록시 패턴 사용 이유
         *  - 흐름 제어 가능. 흐름 제어 이유
         *      - 동기적인 처리를 최대한 비동기적으로 처리하기 위함.(Spring AOP 생각해보자)
         *      - 요청을 프록시 객체가 먼저 받은 뒤 프록시에서 자체적으로 처리할 거 처리하고 클라이언트 기능이 수횅 가능함.
         *          - 실제 메소드가 호출되기 이전에 필요한 기능(전처리 등)을 구현객체 변경없이 추가 가능.(코드 변경 최소화)
         *
         */

        // Subject클래스의 메소드를 호출하는것이아닌 프록시클래스의 메소드를 호출한다.
        Subject subject = new Proxy();
        System.out.println(subject.request()); // 내부적으로 Subject의 메소드를 호출한다.
    }
}
