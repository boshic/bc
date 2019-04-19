package barcode.controllers;


import barcode.dao.entities.Item;
import barcode.dao.services.ItemHandler;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path="/")
public class ItemController {

    private ItemHandler itemHandler;

    public ItemController(ItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    @RequestMapping(value = "/addItem", method = RequestMethod.POST)
    public @ResponseBody
    ResponseItem addItem (@RequestBody Item item) {
        if (item.getId() == null) {
            return  this.itemHandler.addItem(item);
        } else {
            return this.itemHandler.updateItem(item);
        }
    }

    @GetMapping(path="/getItems")
    public @ResponseBody Iterable<Item> getItems(@RequestParam String filter) {
        return this.itemHandler.getItems(filter);
    }

    @GetMapping(path="/getItemById")
    public @ResponseBody Item getItemById(@RequestParam Long id) {
        return this.itemHandler.getItemById(id);
    }

    @GetMapping(path="/deleteItem") // Map ONLY GET Requests
    public @ResponseBody Integer deleteItem(@RequestParam Long id) {
        return this.itemHandler.deleteItem(id);
    }

    @GetMapping(path="/getTopId") // Map ONLY GET Requests
    public @ResponseBody String getTopId() {
        return this.itemHandler.getTopId();
    }

}
