package barcode.dao.repositories;

import com.querydsl.core.types.Predicate;
import barcode.dao.entities.ComingItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComingItemRepository extends CrudRepository<ComingItem, Long>,
                                                JpaSpecificationExecutor<ComingItem>,
                                                QueryDslPredicateExecutor<ComingItem>,
                                                PagingAndSortingRepository<ComingItem, Long> {
    List<ComingItem> findAll();
    List<ComingItem> findAll(Predicate predicate);
    List<ComingItem> findAll(Predicate predicate, Sort sort);
    Page<ComingItem> findAll(Predicate predicate, Pageable pageable);

    ComingItem findTopPriceOutByItemEanOrderByIdDesc(String ean);
    List<ComingItem> findByItemEan(String ean);
}
