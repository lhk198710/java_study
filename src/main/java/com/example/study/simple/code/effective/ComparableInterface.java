package com.example.study.simple.code.effective;

import java.util.Comparator;

/**
 * 아이템 14. Comparable을 구현할지 고려하라.
 */
public class ComparableInterface {
    /**
     * Comparable 인터페이스
     *  compareTo()는 단순 동치성 비교에 더해 순서까지 비교 가능.
     *  Comparable 을 구현했다는 것은 해당 클래스의 순서가 있음을 의미하며 이에 따라 Comparable 을 구현한 객체들의 배열은 sort 를 이용해 정렬이 가능함.
     */

    /**
     * 관계 연산자 "<", ">" 를 사용하면 거추장스럽고 오류를 유발하니 compare()를 사용하자
     * @param x
     * @param y
     * @return
     */
    public int compareTo(int x, int y) {
        return Integer.compare(x, y);
    }

    /**
     * 값의 차를 이용한 비교 시 올바르지 않은 예시
     *  - 정수 오버플로가 일어날 가능성이 있음.
     *  - 부동 소수점 계산 방식에 따라 오류 발생 가능성이 있음.
     */
    public void test1() {
        Comparator<Object> hashCodeOrder = new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return o1.hashCode() - o2.hashCode();
            }
        };
    }

    /**
     * 값의 차를 이용한 비교 시 올바른 예시
     */
    public void test2() {
        Comparator<Object> hashCodeOrder1 = new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return Integer.compare(o1.hashCode(), o2.hashCode());
            }
        };

        Comparator<Object> hashCodeOrder2 = Comparator.comparingInt(o -> o.hashCode());
    }
}
