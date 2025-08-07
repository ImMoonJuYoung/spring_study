package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 상속 전략, 한 테이블에 정보 다 채워놓고 관리
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<Category>();

    //==비즈니스 로직==//

    /**
     * “비즈니스 규칙(로직)은 가능한 한 해당 도메인의 객체(Entity, Value Object 등)에 위치시켜야 한다.”
     * 도메인 객체(Item) 는 그 자체로 의미 있는 상태(state)와 행위(behavior)를 가져야 한다.
     * 비즈니스 규칙이 그 객체의 책임일 경우, 서비스 계층으로 빼는 것은 응집도를 낮추고 객체지향의 원칙을 위배한다.
     * Service는 조율자이지, 모든 로직을 직접 수행하는 주체가 아니다.
     */

    public void addStock(int quantity) { // 재고 증가
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) { // 재고 감소
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
