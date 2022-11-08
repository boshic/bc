package barcode.dao.repositories;

import barcode.dao.entities.ComingReport;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xlinux on 30.10.18.
 */
@Repository
public interface ComingReportRepository extends
    CrudRepository<ComingReport, Long>, QueryDslPredicateExecutor<ComingReport> {

         List<ComingReport> findAll();
         List<ComingReport> findAll(Predicate predicate);
}
