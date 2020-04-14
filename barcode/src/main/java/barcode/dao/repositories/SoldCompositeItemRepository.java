package barcode.dao.repositories;

import barcode.dao.entities.SoldCompositeItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by xlinux on 13.04.20.
 */
@Repository
public interface SoldCompositeItemRepository extends CrudRepository<SoldCompositeItem, Long>{
}
