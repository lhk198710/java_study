package com.example.study.simple.code.effective;

/**
 * 아이템 10. equals는 일반 규약을 지켜 재정의하라.
 *  - equals를 재정의 하는 것은 두 객체가 물리적으로 같은지(같은 메모리 주소 참조)를 비교하는게 아님.
 *      - 논리적 동치성 확인을 위해서는 재정의를 해야하는 경우도 있음.
 *  - 단, equals는 아래 상황일때는 굳이 재정의 하지 않아도 된다.
 *      - 각 인스턴스가 본질적으로 고유한 경우
 *      - 각 인스턴스끼리 논리적 동치성을 검사할 필요가 없는 경우
 *      - 상위 클래스에서 재정의한 equals가 하위 클래스에서도 들어 맞는 경우
 *      - 클래스가 private 이거나 package-private이고 equals를 호출할 일이 없을 경우
 *  - equals를 재정의 하기 위해서는 아래 규약을 따라야 한다.
 *      - 반사성 : null이 아닌 모든 참조 값 x에 대해 x.equals(x)는 true다.
 *      - 대칭성 : null이 아닌 모든 참조 값 x,y에 대해 x.equals(y)가 true면 y.equals(x)도 true다.
 *      - 추이성 : null이 아닌 모든 참조 값 x,y,z에 대해 x.equals(y)가 true이고, y.equals(z)도 true이면 x.equals(z)도 true다.
 *      - 일관성 : null이 아닌 모든 참조 값 x,y에 대해 x.equals(y)를 반복해서 호출하면 항상 true를 반환하거나 항상 false를 반환한다.
 *      - null 아님 : null이 아닌 모든 참조 값 x에 대해 x.equals(null)은 false다.
 */
public class EqualCode {
    /**
     * 굳이 equals를 재정의 해야 하는 경우 아래 순서대로 진행 할 것
     */
    private final int x;
    private final int y;

    public EqualCode(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        /**
         * 1. 자기 자신 비교
         */
        if (this == o) {
            return true;
        }

        /**
         * 2. instanceof 를 이용한 비교
         */
        if (!(o instanceof EqualCode)) {
            return  false;
        }

        /**
         * 3. 클래스 안의 값 비교
         * 소수점은 compare 로 비교. 일반 프리미티브는 == 비교. 인스턴스는 인스턴스가 제공하는 equals로 비교
         */
        EqualCode e = (EqualCode) o;
        return e.x == x && e.y == y;
    }
}
