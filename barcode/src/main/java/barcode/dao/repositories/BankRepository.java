package barcode.dao.repositories;

import barcode.dao.entities.Bank;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankRepository extends CrudRepository<Bank, Long> {

    List<Bank> findByNameContainingIgnoreCaseOrderByNameDesc(String name);

    List<Bank> findByCodeContainingIgnoreCaseOrderByCodeDesc(String code);

    Bank findByCodeIgnoreCase(String code);

    Bank findByNameIgnoreCase(String name);

}
