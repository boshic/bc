package barcode.dao.repositories;

import barcode.dao.entities.Buyer;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.NamedQuery;
import java.util.List;

@Repository
public interface BuyerRepository extends CrudRepository<Buyer, Long>,
        QueryDslPredicateExecutor<Buyer>,
        PagingAndSortingRepository<Buyer, Long> {

    @Query(value = "SELECT b from barcode.dao.entities.Buyer b ORDER BY b.sellings.size DESC")
    List<Buyer> getBuyersOrderedBySellingsSize();

    List<Buyer> findAll(Predicate predicate, Sort sort);

    List<Buyer> findByNameIgnoreCase(String name);

}
