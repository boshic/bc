package barcode.dao.services;

import barcode.dao.entities.Item;
import barcode.dao.entities.ItemSection;
import barcode.dao.entities.QItem;
import barcode.dao.entities.QItemSection;
import barcode.dao.predicates.ItemSectionPredicateBuilder;
import barcode.dao.repositories.ItemSectionRepository;
import barcode.dto.ResponseItem;
import barcode.utils.CommonUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ItemSectionHandler extends EntityHandlerImpl{

    private ItemSectionRepository itemSectionRepository;

    private ItemHandler itemHandler;

    public ItemSectionHandler(
            ItemSectionRepository itemSectionRepository,
            ItemHandler itemHandler) {

        this.itemSectionRepository = itemSectionRepository;
        this.itemHandler = itemHandler;

    }

    private ResponseItem<ItemSection> update(ItemSection newSection, ItemSection section) {

        section.setName(newSection.getName());
        section.setPercOverheadLimit(CommonUtils.validateBigDecimal(newSection.getPercOverheadLimit()));
        itemSectionRepository.save(section);

        return new ResponseItem<ItemSection>("Создана секция '" + section.getName() + "'", true, section);
    }

    public Iterable<ItemSection> getItems(String name) {

        Item item = itemHandler.getItemByEan(name);
        if(item != null)
            return Collections.singletonList(item.getSection());

        ItemSectionPredicateBuilder ispb = new ItemSectionPredicateBuilder();

        return itemSectionRepository.findAll(ispb.buildByFilter(name));
    }

    public ItemSection getItemById(Long id) {

        return itemSectionRepository.findOne(id);
    }

    ItemSection getItemByName(String name) {

        return itemSectionRepository.findOneByNameIgnoreCase(name);
    }

    public ResponseItem<ItemSection> addItem(ItemSection newSection) {

        ItemSection section = new ItemSection();

        if (itemSectionRepository.findByNameIgnoreCase(newSection.getName()).size() == 0)
            return update(newSection, section);

        else
            return setToNameIncorrectEntityFields(newSection, stringsToList(ENTITY_NAME));
//            return new ResponseItem<ItemSection>("Для секции '" + newSection.getName() +
//                    "' имеется совпадение, измените наименование!");
    }

    public ResponseItem<ItemSection> updateItem(ItemSection newSection) {

        ItemSection section = this.getItemById(newSection.getId());

        if ((itemSectionRepository.findByNameIgnoreCase(newSection.getName()).size() == 0) ||
                ((itemSectionRepository.findByNameIgnoreCase(newSection.getName()).size() == 1) &&
                        (itemSectionRepository.findByNameIgnoreCase(newSection.getName()).get(0).getId()
                                .equals(newSection.getId()))))

            return update(newSection, section);
         else
            return setToNameIncorrectEntityFields(newSection, stringsToList(ENTITY_NAME));
    }
}
