package barcode.dao.repositories;

import com.querydsl.core.types.Predicate;
import barcode.dao.entities.SoldItem;
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
public interface SoldItemsRepository extends CrudRepository<SoldItem, Long>,
                                                JpaSpecificationExecutor<SoldItem>,
                                                PagingAndSortingRepository<SoldItem, Long>,
                                                QueryDslPredicateExecutor<SoldItem> {
    List<SoldItem> findAll();
    List<SoldItem> findByComingId(Long id);
    List<SoldItem> findAll(Predicate predicate);
    List<SoldItem> findAll(Predicate predicate, Sort sort);
    Page<SoldItem> findAll(Predicate predicate, Pageable pageable);
}
