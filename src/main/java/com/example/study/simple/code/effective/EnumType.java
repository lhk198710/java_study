package com.example.study.simple.code.effective;

import java.util.Map;
import java.util.stream.Stream;

/**
 * 아이템 34. int 상수 대신 열거 타입을 사용하라.
 */
public class EnumType {
    /**
     * 정수 열거 패턴은 아래와 같은 단점이 존재한다.
     *  - 타입 안전 보장 방법이 없으며 표현력이 안 좋음.
     *  - 정수 상수는 문자열 출력이 까다로움.
     *  - 상수 값이 바뀔 경우 프로그램이 다시 컴파일 되어야 함.
     */

    /**
     * 위 정수 열거 패턴의 단점을 보완하기 위해 Enum type을 사용하는 것이 좋음.
     *  - 자바의 열거 타입은 완전한 형태의 클래스다.
     *  - 열거 타입 자체는 클래스고 상수 하나당 자신의 인스턴스를 하나씩 만들어 public static final 필드로 공개함.
     *  - 밖에서 접근할 수 있는 생성자는 제공하지 않음.
     *  - 컴파일 시 안전성 제공.
     *  - toString 메서드는 출력하기에 적합한 문자열을 내어줌.
     *  - 임의의 필드 또는 메서드 추가가 가능하며 임의의 인터페이스를 구현하게 할 수도 있음.
     */
    public enum Apple {FULL, PIPPIN, GRANNY_SMITH}

    public enum Operation {
        PLUS("+") { public double apply(double x, double y) { return x+y; } },
        MINUS("-") { public double apply(double x, double y) { return x-y; } },
        TIMES("*") { public double apply(double x, double y) { return x*y; } },
        DIVIDE("/") { public double apply(double x, double y) { return x/y; } };

        private final String symbol;
        Operation(String symbol) { this.symbol = symbol; }

        @Override
        public String toString() {
            return symbol;
        }

        public abstract  double apply(double x, double y);
    }
}