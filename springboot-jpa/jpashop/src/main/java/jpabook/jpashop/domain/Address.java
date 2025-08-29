package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() { }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}


/*
값 타입은 변경 불가능하게 설계해야 한다.
JPA 스펙상 엔티티나 임베디드 타입(`@Embeddable` )은
자바 기본 생성자(default constructor)를 `public` 또는 `protected` 로 설정해야 한다.
`public` 으로 두는 것 보다는 `protected` 로 설정하는 것이 그나마 더 안전하다.

JPA는 프록시 기반이고, 리플렉션으로 객체를 생성한다.
JPA (Hibernate 등)는 실제로 객체를 생성할 때 개발자가 생성자를 직접 호출하지 않더라도, 리플렉션을 통해 인스턴스를 생성한다.
이 과정에서 JPA는 기본 생성자 (no-arg constructor) 가 있어야만 인스턴스를 만들 수 있다.
✅ 즉, JPA는 반드시 기본 생성자가 필요하다.
- newInstance()를 사용할 때는 매개변수가 없는 생성자가 필요하다.
- 이 생성자는 private이면 JPA가 호출할 수 없다.
- 따라서 protected 또는 public 으로 선언되어야 JPA가 리플렉션을 통해 접근 가능하다.

리플렉션(reflection)은 자바 프로그램이 실행 중에 클래스, 메서드, 필드 등의 정보를 동적으로 조회하고 조작할 수 있게 해주는 기능이다.
어디에 있나?	: java.lang.reflect 패키지
주요 용도?	:프레임워크, DI 컨테이너, ORM(JPA), 테스트 도구 등에서 동적 클래스 처리
 */