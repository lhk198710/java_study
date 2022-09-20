package com.example.study.simple.code.effective;

/**
 * 아이템 13. clone 재정의는 주의해서 진행하라.
 *  - 새로운 인터페이스를 만들 때는 절대 Cloneable을 확장하지 말 것.
 *  - 새로운 클래스도 이를 구현해서는 안된다.
 *      - 기본 원칙은 '복제 기능은 생성자와 팩토리를 이용하는게 최고'라는 것이다.
 */
public class NotExtendClone {
    /**
     * Cloneable 인터페이스
     *  - 클래스에서 clone을 재정의 하기 위해선 해당 클래스에 Cloneable 인터페이스를 상속받아 구현해야 한다.
     *  - 이 clone 메소드는 Cloneable 인터페이스가 아닌 Object에 선언 되어 있다.
     */

    /**
     * Cloneable 인터페이스 역할
     *  - 상속받은 클래스가 복제해도 되는 클래스임을 명시하는 용도.
     *  - Cloneable을 상속한 클래스의 clone 메서드를 호출하면 해당 클래스를 필드 단위로 복사하여 반환.
     *  - 만약, Cloneable을 상속받지 않고 clone 메서드를 호출하였다면 'CloneNotSupportedException'이 발생한다.
     */
}
