package hello.core;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.MemberRepository;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    /*
    @Bean memberService -> new MemoryMemberRepository() 생성
    @Bean orderService -> new MemoryMemberRepository() 생성
    위의 메서드 둘을 호출하면 MemoryMemberRepository 객체가 두 개 생성되면서 싱글톤이 깨질 것 같음
    그러나 깨지지 않음

    스프링이 CGLIB라는 바이트코드 조작 라이브러리를 통해 클래스를 상속받은 임의의 다른 클래스를 만들고
    그 다른 클래스를 스프링 빈으로 등록함
    @Bean이 붙은 메서드마다 이미 스프링 빈이 존재하면 존재하는 빈을 반환하고,
    없으면 생성해서 스프링에 등록하고 반환하는 코드가 동적으로 만들어짐
    덕분에 싱글턴 보장
     */

    @Bean
    public MemberService memberService() {
        System.out.println("call AppConfig.memberService");
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        System.out.println("call AppConfig.memberRepository");
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        System.out.println("call AppConfig.orderService");
        return new OrderServiceImpl(memberRepository(), discountPolicy());
//        return null;
    }

    @Bean
    public DiscountPolicy discountPolicy() {
//        return new FixDiscountPolicy();
        return new RateDiscountPolicy();
    }
}
