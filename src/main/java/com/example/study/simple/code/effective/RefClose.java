package com.example.study.simple.code.effective;

import java.util.Arrays;
import java.util.EmptyStackException;

/**
 * 아이템 7. 다 쓴 객체 참조를 해제하라.
 * 아래 스택을 구현한 RefClose 는 memoryLeakingPop 함수를 통해 pop을 하는 경우 메모리 누수가 발생함.
 *  - 오래 구동되다 보면 성능 저하 & OutOfMemoryError 발생
 */
public class RefClose {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public RefClose() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    /**
     * 누수되는 pop
     *  - 스택이 커졌다가 줄어들 떄, 스택에서 꺼내진 객체들은 프로그램에서 더 이상 사용하지 않더라도 GC 가 회수 안함
     * @return
     */
    public Object memoryLeakingPop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return elements[--size];
    }

    /**
     * 누수 방지 pop
     *  - 참조를 다 썼을 때 null을 이용해 참조를 해제한다.
     * @return
     */
    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
