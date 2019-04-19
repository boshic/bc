package barcode.dao.repositories;

import barcode.dao.entities.Organization;
import org.springframework.data.repository.CrudRepository;

public interface OrganizationRepository extends CrudRepository<Organization, Long> {

}
