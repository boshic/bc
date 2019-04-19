package barcode.dao.repositories;

import barcode.dao.entities.ComingReport;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by xlinux on 30.10.18.
 */
@Repository
public interface ComingReportRepository extends CrudRepository<ComingReport, Long> {

}
