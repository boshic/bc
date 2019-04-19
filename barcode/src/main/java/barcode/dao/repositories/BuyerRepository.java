package barcode.dao.repositories;

import barcode.dao.entities.Buyer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyerRepository extends CrudRepository<Buyer, Long> {
    List<Buyer> findByNameContainingIgnoreCaseOrderByIdAsc(String filter);
    List<Buyer> findByNameIgnoreCase(String name);
}
