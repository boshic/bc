package barcode.dao.repositories;

import com.querydsl.core.types.Predicate;
import barcode.dao.entities.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, Long>,
                                        QueryDslPredicateExecutor<Invoice> {

    List<Invoice> findAll();
    List<Invoice> findAll(Predicate predicate);
    Page<Invoice> findAll(Predicate predicate, Pageable pageable);
    List<Invoice> findByDateBetweenOrderByDateDesc(Date fromDate, Date toDate);
//    List<Invoice> findByInvoiceRowsSizeGreaterThan(Integer size);
}