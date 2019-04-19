package barcode.controllers;

import barcode.dao.entities.ItemSection;
import barcode.dao.services.ItemSectionHandler;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by xlinux on 24.09.18.
 */
@Controller
@RequestMapping(path="/")
public class ItemSectionController {

    private ItemSectionHandler itemSectionHandler;

    public ItemSectionController(ItemSectionHandler itemSectionHandler) {

        this.itemSectionHandler = itemSectionHandler;
    }

    @GetMapping(path="/getSections")
    public @ResponseBody Iterable<ItemSection> getItems(@RequestParam String filter) {
        return this.itemSectionHandler.getItems(filter);
    }

    @GetMapping(path="/getSectionById")
    public @ResponseBody ItemSection getItemById(@RequestParam Long id) {

        return this.itemSectionHandler.getItemById(id);
    }

    @RequestMapping(value = "/addSection", method = RequestMethod.POST)
    public @ResponseBody ResponseItem addSection (@RequestBody ItemSection section) {
        if (section.getId() == null) {
            return  this.itemSectionHandler.addItem(section);
        } else {
            return this.itemSectionHandler.updateItem(section);
        }
    }
}
