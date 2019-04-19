package barcode.dao.repositories;

import barcode.dao.entities.ItemSection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemSectionRepository extends CrudRepository<ItemSection, Long> {

    List<ItemSection> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    List<ItemSection> findByNameIgnoreCase(String name);

    ItemSection findOneByNameIgnoreCase(String name);
}
