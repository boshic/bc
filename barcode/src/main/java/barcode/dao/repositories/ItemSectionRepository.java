package barcode.dao.repositories;

import barcode.dao.entities.Item;
import barcode.dao.entities.ItemSection;
import com.querydsl.core.types.Predicate;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemSectionRepository extends CrudRepository<ItemSection, Long>,
        QueryDslPredicateExecutor<ItemSection> {

    List<ItemSection> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    List<ItemSection> findByNameIgnoreCase(String name);

    List<ItemSection> findAll(Predicate predicate);

    ItemSection findOneByNameIgnoreCase(String name);
}
