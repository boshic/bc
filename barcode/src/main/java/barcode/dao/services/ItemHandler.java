package barcode.dao.services;

import barcode.dao.entities.Item;
import barcode.dao.entities.QComingItem;
import barcode.dao.entities.QItem;
import barcode.dao.entities.embeddable.InventoryRow;
import barcode.dao.predicates.ItemPredicateBuilder;
import barcode.dao.predicates.ItemSectionPredicateBuilder;
import barcode.dao.repositories.ComingItemRepository;
import barcode.dao.repositories.ItemRepository;
import barcode.dao.utils.ComingItemFilter;
import barcode.dto.ResponseItem;
import com.querydsl.core.types.Predicate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ItemHandler {

    public static ItemPredicateBuilder ipb = new ItemPredicateBuilder();

    private static final Integer EAN_LENGTH = 13;
    private static final String BAD_EAN_SYNONYM = "Товар имеет приходы, или указанный ШК уже имеет синоним!";
    private static final String ITEM_ALREADY_EXIST = "С заданным ШК товар уже существует, добавление не состоится";

    private ItemRepository itemRepository;

    private ComingItemRepository comingItemRepository;

    public ItemHandler(ItemRepository itemRepository, ComingItemRepository comingItemRepository) {

        this.comingItemRepository = comingItemRepository;

        this.itemRepository = itemRepository;

    }

    private ResponseItem<Item> update(Item srcItem, Item item) {

        item.setName(srcItem.getName());

        item.setUnit(srcItem.getUnit());

        item.setEan(srcItem.getEan());

        if(isAllowedEanSynonym(srcItem, item))
           item.setEanSynonym(srcItem.getEanSynonym() == null ? "" : srcItem.getEanSynonym());
        else {
            srcItem.setEanSynonym(BAD_EAN_SYNONYM);
            return new ResponseItem<Item>(BAD_EAN_SYNONYM + " " + srcItem.getName(), false, srcItem);
        }

        item.setAlterName(srcItem.getAlterName() == null ? "" : srcItem.getAlterName());
        item.setPredefinedQuantity(srcItem.getPredefinedQuantity() == null ? BigDecimal.ZERO : srcItem.getPredefinedQuantity());

//        qItem.components.any().component.ean.eq(srcItem.getEan());
        if (item.getCanBeComposite() == null)
            item.setCanBeComposite(true);

        if(srcItem.getSection()!= null)
            item.setSection(srcItem.getSection());

        itemRepository.save(item);

        return new ResponseItem<Item>("Создан новый товар: " + item.getName(), true, item);
    }

    private boolean isAllowedEanSynonym(Item srcItem, Item destItem) {

        QItem qItem = QComingItem.comingItem.item;

        Predicate predicate = (destItem.getId() == null)
                ? qItem.ean.eq(srcItem.getEan()) :
                qItem.ean.eq(srcItem.getEan()).or(qItem.id.eq(destItem.getId()));

        return srcItem.getEanSynonym()== null || srcItem.getEanSynonym().length() != EAN_LENGTH ||
                (comingItemRepository.findAll(predicate).size() == 0
                        && itemRepository.findOneByEan(srcItem.getEanSynonym())
                            .getEanSynonym().length() != EAN_LENGTH);
    };

    private ResponseItem<Item> duplicateEanError(Item item) {
        item.setEan(ITEM_ALREADY_EXIST);
        return new ResponseItem<Item>(ITEM_ALREADY_EXIST, false, item);
    }

    public ResponseItem<Item> addItem(Item item) {

        if (itemRepository.findByEanOrderByNameDesc(item.getEan()).size() == 0)
            return update(item, new Item());

        else
            return duplicateEanError(item);
    }

    public ResponseItem<Item> updateItem(Item item) {

        List<Item> items = itemRepository.findByEanOrderByNameDesc(item.getEan());

        if ((items.size() == 0) ||
                ((items.size() == 1) && (items.get(0).getId().equals(item.getId()))))
            return update(item, getItemById(item.getId()));

        else
            return duplicateEanError(item);

    }

    public List<Item> getItems(String filter) {

        List<Item> items = itemRepository.findByEanOrderByNameDesc(filter);

        if(items.size() > 0)
            return items;

        if(filter != null && filter.length() >= 2)
            return itemRepository.findAll(ipb.buildByFilter(filter));
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

    void saveItem(Item item) {
        itemRepository.save(item);
    };

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

    Item getItemByEanSynonym (String ean) {
        Item item = itemRepository.findOneByEan(ean);

        if(item != null && item.getEanSynonym().length() == EAN_LENGTH) {
            BigDecimal predefinedQuantity = item.getPredefinedQuantity();
            item = itemRepository.findOneByEan(item.getEanSynonym());
            item.setPredefinedQuantity(predefinedQuantity);
        }

        return item;
    }

    Boolean isEanValid(String ean) {

        return (ean != null && ean.length() == EAN_LENGTH);
    }

    InventoryRow getInventoryRowByStock(Item item, Long stockId) {

        if(item.getInventoryRows() != null)
            for(InventoryRow row : item.getInventoryRows())
                if(row.getStock().getId().equals(stockId))
                    return row;

        return null;
    }

    void checkEanInFilter(ComingItemFilter filter) {

        String eanSynonym;

        Item item = getItemByEan(filter.getEan());

        if(isEanValid(filter.getEan())) {
            eanSynonym = item == null ? null : item.getEanSynonym();

            if(isEanValid(eanSynonym))
                filter.setEan(eanSynonym);
        }
    }

}
