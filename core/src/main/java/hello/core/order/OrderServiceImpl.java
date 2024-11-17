package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService {

//    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
//    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
    /*
    * 'OrderServiceImple'이 'DiscountPolicy'인터페이스 뿐만 아니라
    * 구체 클래스인 'FixDiscoundPolicy, RateDiscountPolicy'도 의존하고 있음
    * 즉, DIP 위반 -> 인터페이스에만 의존할 수 있도록 수정해야 됨
    * OrderServiceImpl에 DiscoundPolicyd의 구현 객체를 대신 생성하고 주입
    * */

    private final DiscountPolicy discountPolicy;

//    private final MemberRepository memberRepository = new MemoryMemberRepository();
    private final MemberRepository memberRepository;

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);
        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
