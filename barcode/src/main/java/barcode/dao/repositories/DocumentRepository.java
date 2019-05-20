package barcode.dao.repositories;

import com.querydsl.core.types.Predicate;
import barcode.dao.entities.Document;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Long>,
        QueryDslPredicateExecutor<Document> {

    List<Document> findAll();

    Document findOne(Predicate predicate);

    List<Document> findByNameIgnoreCaseAndDate(String name, Date date);

    List<Document> findAll(Predicate predicate);

    List<Document> findByDateBetweenOrderByDateDesc(Date fromDate, Date toDate);

    List<Document> findTop100ByNameContainingIgnoreCaseAndDateBetweenOrderByDateDesc(String name,
                                                                               Date fromDate, Date toDate);

    List<Document> findByNameContainingIgnoreCaseAndDateBetweenOrderByDateDesc(String name,
                                                                               Date fromDate, Date toDate);
}
