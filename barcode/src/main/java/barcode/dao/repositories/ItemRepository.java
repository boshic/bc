package barcode.dao.repositories;

import barcode.dao.entities.Item;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
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
    Page<Item> findAll(Predicate predicate, Pageable pageable);
    Item findOneByEan(String ean);

    @Query(value = "SELECT i from barcode.dao.entities.Item i where i.ean=?1 and i.components.size=0")
    Item getItemByEanWithoutComponents(String ean);


}
