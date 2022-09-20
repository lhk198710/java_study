package com.example.study.simple.code.effective;

import java.util.regex.Pattern;

/**
 * 아이템 6. 불필요한 객체 생성을 피하라.
 */
public class UnnecessaryObject {
    private static final Pattern ROMAN = Pattern.compile(
            "^(?=.)M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

    public void createUnnecessaryObject() {
        /**
         * a, b, c 모두 hi라는 문자열을 가지고 있으나 이 세문자열이 참조하는 주소는 모두 다르기 때문에 메모리 낭비임.
         */
        String a = new String("hi");
        String b = new String("hi");
        String c = new String("hi");

        /**
         * 문자열 선언시 new 키워드 보다는 리터럴로 선언해야 함.
         */
        String d = "hi";
        String e = "hi";
        String f = "hi";
    }

    public void useBoolean() {
        /**
         * 문자열을 매개변수로 받는 생성자를 통해 Boolean 인스턴스를 만들고 있음.
         * 매번 인스턴스를 만드는 것은 메모리 낭비임.
         */
        Boolean b = new Boolean("true");

        /**
         * Boolean 선언 시 Boolean.valueOf()를 사용 할 것
         */
        Boolean c = Boolean.valueOf("true");
    }

    public void regex(String s) {
        /**
         * String.matches는 성능적으로 문제가 있는 메서드임.
         * 이 메서드가 내부에서 사용하는 Pattern 인스턴스는 GC 대상이며 많이 쓸 수록 비용이 커짐.
         */
        s.matches("^(?=.)M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

        /**
         * 비용 절감을 위해서는 Pattern 인스턴스를 미리 캐싱해두고 인스턴스를 재사용하는 방향으로 써야함.
         */
        ROMAN.matcher(s).matches();
    }
}
