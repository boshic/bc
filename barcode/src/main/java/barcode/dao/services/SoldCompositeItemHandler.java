package barcode.dao.services;

import barcode.dao.entities.SoldCompositeItem;
import barcode.dao.repositories.SoldCompositeItemRepository;
import org.springframework.stereotype.Service;

/**
 * Created by xlinux on 13.04.20.
 */
@Service
public class SoldCompositeItemHandler {

    private SoldCompositeItemRepository soldCompositeItemRepository;

    public SoldCompositeItemHandler(SoldCompositeItemRepository soldCompositeItemRepository) {

        this.soldCompositeItemRepository = soldCompositeItemRepository;
    }

    void save (SoldCompositeItem soldCompositeItem) {
        if(soldCompositeItem != null)
            soldCompositeItemRepository.save(soldCompositeItem);
    }

}
