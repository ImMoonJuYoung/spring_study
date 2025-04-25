package hello.itemservice.domain.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepository {

    private static final Map<Long, Item> store = new HashMap<>(); // static을 붙였다는 건 인스턴스가 여러 개 생성되더라도 이 변수는 단 하나만 존재한다는 의미
    private static long sequence = 0L;

    public Item save(Item item) {
        item.setId(sequence++);
        store.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return store.get(id);
    }

    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }

    public void update(Long itemId, Item updateParam) {
        Item item = findById(itemId);
        item.setItemName(updateParam.getItemName()); // 실무에선 updateParam용 dto 객체를 따로 만든다. id 변경해버릴 수도 있기 때문.
        item.setPrice(updateParam.getPrice());
        item.setQuantity(updateParam.getQuantity());
    }

    public void clearStore() {
        store.clear();
    }

}
