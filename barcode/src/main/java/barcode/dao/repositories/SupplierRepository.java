package barcode.dao.repositories;

import barcode.dao.entities.Supplier;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends CrudRepository<Supplier, Long>,
                                            QueryDslPredicateExecutor<Supplier> {

    List<Supplier> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
    Supplier findByNameIgnoreCase(String name);

}
