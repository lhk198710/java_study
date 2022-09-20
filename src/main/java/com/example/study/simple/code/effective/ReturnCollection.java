package com.example.study.simple.code.effective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 아이템 57. null이 아닌 빈 컬렉션을 반환하라.
 */
public class ReturnCollection {
    private List<String> testStrs;

    /**
     * null을 리턴하는 경우 이 함수를 사용하는 클라이언트는 null을 처리하는 추가 코드를 작성해야 함.
     *  - 클라이언트가 방어처리를 해야 하는 관계로 코드가 복잡해지고 오류 발생 가능성이 존재함.
     * @return
     */
    public List<String> getTestStrs1() {
         return testStrs.isEmpty() ? null : new ArrayList<String>();
    }

    /**
     * 올바르게 반환을 하기 위해서는 아래와 같이 해야함.
     * @return
     */
    public List<String> getTestStrs2() {
        return testStrs.isEmpty() ? Collections.emptyList() : new ArrayList<String>();
    }

    /**
     * 배열의 경우 null이 아닌 길이가 0인 배열을 반환해야함.
     */
    public String[] getTestStr3() {
        return testStrs.toArray(new String[0]);
    }
}
