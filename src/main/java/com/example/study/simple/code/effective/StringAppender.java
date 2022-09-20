package com.example.study.simple.code.effective;

/**
 * 아이템 63. 문자열 연결은 느리니 주의하라.
 *  - 문자열 연결 연산자(+) 사용 시 문자열 n개를 잇는 시간은 n제곱에 비례하여 성능 저하가 올 수 있다.
 */
public class StringAppender {
    /**
     * 올바르지 않은 문자열 연결 예
     *  - 연결 연산자 사용
     * @return
     */
    public String statement() {
        String result = "";
        for(int i=0; i<10; i++) {
            result += result + i;
        }

        return result;
    }

    /**
     * StringBuilder를 사용한다면 성능이 크게 개선이 된다.
     * @return
     */
    public String statement2() {
        StringBuilder b = new StringBuilder();
        String result = "";
        for(int i=0; i<10; i++) {
            b.append(i);
        }

        return result;
    }

    /**
     * 참고 : StringBuilder와 StringBuffer 사용처
     *  - StringBuilder : 문자열 연산이 많고 멀티스레드 환경일 경우 사용.(동기화 지원 안 함). 단일 성능으론 StringBuffer보단 뛰어남.
     *  - StringBuffer : 문자열 연산이 많고 단일스레드이거나 동기화를 고려하지 않아도 되는 경우 사용.(동기화 지원 함)
     *
     */
}
