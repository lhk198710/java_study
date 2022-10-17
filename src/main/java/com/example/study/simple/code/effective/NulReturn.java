package com.example.study.simple.code.effective;

import java.util.*;

/**
 * 아이템 54. Null이 아닌 빈 컬렉션이나 배열을 반환하라.
 *  - Null 반환 후 추가 로직 생성 작업 방지를 위함.
 */
public class NulReturn {
    String something;
    private final List<Cheese> cheeseInStock = new ArrayList<>();

    public void checkIsSomethingNull() {
        this.something = Objects.requireNonNull(something, "CHECK FAIL"); // NPE
    }

    /**
     * null 을 반환하는 것이 올바르지 않은 코드는 아니지만 null을 반환하게 된다면 반환된 null을 처리해야 하는 코드를 반드시 추가적으로 작성해야 함.
     * @return
     */
    public List<Cheese> getNullCheeses() {
        return cheeseInStock.isEmpty() ? null : new ArrayList<>(cheeseInStock);
    }

    /**
     * 빈 컬렉션 반환(List)
     * @return
     */
    public List<Cheese> getEmptyCheeseList() {
        return Collections.emptyList();
    }

    /**
     * 빈 컬렉션 반환(List)
     * @return
     */
    public Set<Cheese> getEmptyCheeseSet() {
        return Collections.emptySet();
    }

    /**
     * 빈 컬렉션 반환(Map)
     * @return
     */
    public Map<String, Cheese> getEmptyCheeseMap() {
        return Collections.emptyMap();
    }

    class Cheese {

    }
}
