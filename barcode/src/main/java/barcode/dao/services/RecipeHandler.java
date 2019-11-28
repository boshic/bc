package barcode.dao.services;

import barcode.dao.entities.Recipe;
import barcode.dao.repositories.RecipeRepository;
import org.springframework.stereotype.Service;

/**
 * Created by xlinux on 27.11.19.
 */
@Service
public class RecipeHandler {

    private RecipeRepository recipeRepository;

    public RecipeHandler(RecipeRepository recipeRepository) {

        this.recipeRepository = recipeRepository;
    }

    void save (Recipe recipe) {

        recipeRepository.save(recipe);
    }


}
