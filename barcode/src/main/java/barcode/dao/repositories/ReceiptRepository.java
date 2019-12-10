package barcode.dao.repositories;

import barcode.dao.entities.Receipt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by xlinux on 27.11.19.
 */
@Repository
public interface ReceiptRepository extends CrudRepository<Receipt, Long> {
}
