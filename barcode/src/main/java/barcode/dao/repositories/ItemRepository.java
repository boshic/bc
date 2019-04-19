package barcode.dao.repositories;

import barcode.dao.entities.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {
    Item findTopByOrderByIdDesc();
    List<Item> findAll();
    List<Item> findByNameContainingIgnoreCase(String name);
    List<Item> findByEanOrderByNameDesc(String filter);
    Item findOneByEan(String ean);
}
