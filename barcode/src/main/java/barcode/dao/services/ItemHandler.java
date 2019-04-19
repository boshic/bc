package barcode.dao.services;

import barcode.dao.entities.Item;
import barcode.dao.repositories.ItemRepository;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemHandler {

    private ItemRepository itemRepository;

    public ItemHandler(ItemRepository itemRepository) {

        this.itemRepository = itemRepository;

    }

    private ResponseItem<Item> update(Item newItem, Item item) {

        item.setName(newItem.getName());

        item.setUnit(newItem.getUnit());

        item.setEan(newItem.getEan());

        if(newItem.getSection()!= null)
            item.setSection(newItem.getSection());

        itemRepository.save(item);

        return new ResponseItem<Item>("Создание нового товара " + item.getName(), true, item);
    }

    public ResponseItem<Item> addItem(Item model) {

        if (itemRepository.findByEanOrderByNameDesc(model.getEan()).size() == 0)
            return update(model, new Item());

        else
            return new ResponseItem<Item>("Для " + model.getName() + " уже существует товар с таким ШК!");
    }

    public ResponseItem<Item> updateItem(Item newItem) {

        List<Item> items = itemRepository.findByEanOrderByNameDesc(newItem.getEan());

        if ((items.size() == 0) ||
                ((items.size() == 1) && (items.get(0).getId().equals(newItem.getId()))))
            return update(newItem, getItemById(newItem.getId()));

         else
            return new ResponseItem<Item>("Для " + newItem.getName() +
                    " уже существует товар с таким ШК!");
    }

    public List<Item> getItems(String filter) {

        List<Item> items;

        items = itemRepository.findByEanOrderByNameDesc(filter);

        if (items.size() == 0) {
            switch (filter) {

                case "all" : items = itemRepository.findAll(); break;

                default: items = itemRepository.findByNameContainingIgnoreCase(filter);
            }
        }
        return items;
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

    public Item getItemByEan (String ean) {

        System.out.println(ean);
        return  itemRepository.findOneByEan(ean);
//        return itemRepository.findOneByEan(ean);
    }

}
