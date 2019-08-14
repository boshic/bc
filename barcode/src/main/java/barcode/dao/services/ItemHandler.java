package barcode.dao.services;

import barcode.dao.entities.ComingItem;
import barcode.dao.entities.Item;
import barcode.dao.predicates.ItemPredicateBuilder;
import barcode.dao.repositories.ComingItemRepository;
import barcode.dao.repositories.ItemRepository;
import barcode.dto.ResponseItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ItemHandler {

    private static final Integer EAN_LENGTH = 13;
    private static final String BAD_EAN_SYNONYM = "Товар имеет приходы, или указанный ШК уже имеет синоним!";
    private static final String ITEM_ALREADY_EXIST = "С заданным ШК товар уже существует, добавление не состоится";

    private ItemRepository itemRepository;

    private ComingItemRepository comingItemRepository;

    public ItemHandler(ItemRepository itemRepository, ComingItemRepository comingItemRepository) {

        this.comingItemRepository = comingItemRepository;

        this.itemRepository = itemRepository;

    }

    private ResponseItem<Item> update(Item newItem, Item item) {

        item.setName(newItem.getName());

        item.setUnit(newItem.getUnit());

        item.setEan(newItem.getEan());

        String eanSynonym = newItem.getEanSynonym() == null ? "" : newItem.getEanSynonym();
        if(isAllowedEanSynonym(newItem.getEan(), eanSynonym))
           item.setEanSynonym(eanSynonym);
        else {
            newItem.setEanSynonym(BAD_EAN_SYNONYM);
            return new ResponseItem<Item>(BAD_EAN_SYNONYM + " " + newItem.getName(), false, newItem);
        }

        item.setPredefinedQuantity(newItem.getPredefinedQuantity() == null ? BigDecimal.ZERO : newItem.getPredefinedQuantity());

        if (item.getCanBeComposite() == null)
            item.setCanBeComposite(true);

        if(newItem.getSection()!= null)
            item.setSection(newItem.getSection());

        itemRepository.save(item);

        return new ResponseItem<Item>("Создание нового товара " + item.getName(), true, item);
    }

    private boolean isAllowedEanSynonym(String ean, String eanSynonym) {

        return eanSynonym.length() != EAN_LENGTH ||
                (comingItemRepository.findByItemEan(ean).size() == 0
                && itemRepository.findOneByEan(eanSynonym).getEanSynonym().length() != EAN_LENGTH);
    };

    private ResponseItem<Item> duplicateEanError(Item item) {
        item.setEan(ITEM_ALREADY_EXIST);
        return new ResponseItem<Item>(ITEM_ALREADY_EXIST, false, item);
    }

    public ResponseItem<Item> addItem(Item model) {

        if (itemRepository.findByEanOrderByNameDesc(model.getEan()).size() == 0)
            return update(model, new Item());

        else
            return duplicateEanError(model);
    }

    public ResponseItem<Item> updateItem(Item newItem) {

        List<Item> items = itemRepository.findByEanOrderByNameDesc(newItem.getEan());

        if ((items.size() == 0) ||
                ((items.size() == 1) && (items.get(0).getId().equals(newItem.getId()))))
            return update(newItem, getItemById(newItem.getId()));

        else
            return duplicateEanError(newItem);

    }

    public List<Item> getItems(String filter) {

        List<Item> items = itemRepository.findByEanOrderByNameDesc(filter);

        if(items.size() > 0)
            return items;

        if(filter != null && filter.length() >= 2)
            return itemRepository.findAll(new ItemPredicateBuilder().buildByFilter(filter));
//            return itemRepository.findByNameContainingIgnoreCase(filter);

        return itemRepository.findTop100ByNameContainingIgnoreCase(filter);
    }

    public Item getItemById(Long id) {
        return itemRepository.findOne(id);
    }

    public void addItemFromParsedData (String name) {

        Item item = new Item(name);

        itemRepository.save(item);
    }

    public Integer deleteItem(long id) {

        Item item = this.getItemById(id);

        if(item.getComings().size() == 0)
            itemRepository.delete(item);

        return item.getComings().size();
    }

    public String getTopId() {
        return itemRepository.findTopByOrderByIdDesc().getId().toString();
    }

    Item getItemByEan (String ean) {

        System.out.println(ean);
        return  itemRepository.findOneByEan(ean);
    }

    Item getItemByEanSynonim (String ean) {
        Item item = itemRepository.findOneByEan(ean);

        if(item != null && item.getEanSynonym().length() == EAN_LENGTH) {
            BigDecimal predefinedQuantity = item.getPredefinedQuantity();
            item = itemRepository.findOneByEan(item.getEanSynonym());
            item.setPredefinedQuantity(predefinedQuantity);
        }

        return item;
    }

}
