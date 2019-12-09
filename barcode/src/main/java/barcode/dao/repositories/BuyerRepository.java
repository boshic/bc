package barcode.dao.repositories;

import barcode.dao.entities.Buyer;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyerRepository extends CrudRepository<Buyer, Long>,
        QueryDslPredicateExecutor<Buyer>,
        PagingAndSortingRepository<Buyer, Long> {

    List<Buyer> findByNameContainingIgnoreCaseOrderByIdAsc(String filter);

    List<Buyer> findAll(Predicate predicate, Sort sort);

    List<Buyer> findByNameIgnoreCase(String name);
}
