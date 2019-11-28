package barcode.dao.repositories;

import barcode.dao.entities.Recipe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by xlinux on 27.11.19.
 */
@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Long> {
}
