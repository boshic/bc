package barcode.dao.repositories;

import barcode.dao.entities.Item;
import com.querydsl.core.types.Predicate;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long>,
        QueryDslPredicateExecutor<Item> {
    Item findTopByOrderByIdDesc();
    List<Item> findAll();
    List<Item> findByNameContainingIgnoreCase(String name);
    List<Item> findTop100ByNameContainingIgnoreCase(String name);
    List<Item> findByEanOrderByNameDesc(String filter);
    List<Item> findAll(Predicate predicate);
    Item findOneByEan(String ean);

}
