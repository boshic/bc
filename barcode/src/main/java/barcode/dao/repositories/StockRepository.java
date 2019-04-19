package barcode.dao.repositories;

import barcode.dao.entities.Stock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends CrudRepository<Stock, Long> {
    List<Stock> findByAllowAll(Boolean allowAll);
    List<Stock> findAll();
    Stock findByName(String name);
}
