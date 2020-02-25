package barcode.controllers;

import barcode.dao.entities.ComingItem;
import barcode.dao.services.ComingItemHandler;
import barcode.dao.utils.ComingItemFilter;
import barcode.dto.DtoItemForNewComing;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(path="/")
public class ComingItemController {

    private ComingItemHandler comingItemHandler;
    public ComingItemController(ComingItemHandler comingItemHandler) {
        this.comingItemHandler = comingItemHandler;
    }

    //start of coming API
    @RequestMapping(value = "/addComing", method = RequestMethod.POST)
    public @ResponseBody
    ResponseItem addComingItem (@RequestBody ComingItem coming) {
        return this.comingItemHandler.addItem(coming);
    }

    @RequestMapping(value = "/updateComing", method = RequestMethod.POST)
    public @ResponseBody ResponseItem updateComing(@RequestBody ComingItem item) {
        return this.comingItemHandler.updateItem(item);
    }

    @RequestMapping(value = "/setInventoryItems", method = RequestMethod.POST)
    public @ResponseBody void setInventoryItems(@RequestBody Set<ComingItem> comings) {
        this.comingItemHandler.setInventoryItems(comings);
    }

    @RequestMapping(value = "/addComings", method = RequestMethod.POST)
    public @ResponseBody ResponseItem addComings(@RequestBody Set<ComingItem> comings) {
        return this.comingItemHandler.addItems(comings);
    }

    @GetMapping(value = "/deleteComing")
    public @ResponseBody ResponseItem deleteComing (@RequestParam Long id) {
        return this.comingItemHandler.deleteItem(id);
    }

    @GetMapping(path="/getComingById")
    public @ResponseBody ResponseItem<ComingItem> getComingById(@RequestParam Long id) {
        return new ResponseItem<ComingItem>("", true, this.comingItemHandler.getComingItemById(id));
    }

    @GetMapping(path="/getComingBySpec")
    public @ResponseBody ResponseItem gcbc() {
        List<ComingItem> results = this.comingItemHandler.getComingBySpec();
        return null;
    }

    @GetMapping(path="/getComingForSell") // Map ONLY GET Requests
    public @ResponseBody ResponseItem getItemsWithQuant(@RequestParam String filter,
                                                        @RequestParam Long stockId) {
        return this.comingItemHandler.getComingForSell(filter, stockId);
    }

    @GetMapping(path="/getComingForSellNonComposite") // Map ONLY GET Requests
    public @ResponseBody ResponseItem getComingForSellNonComposite(@RequestParam String filter,
                                                        @RequestParam Long stockId) {
        return this.comingItemHandler.getComingForSellNonComposite(filter, stockId);
    }

    @RequestMapping(value = "/findComingItemByFilter", method = RequestMethod.POST)
    public @ResponseBody ResponseItem<ComingItem> findComingItemByFilter(@RequestBody ComingItemFilter filter) {
        return this.comingItemHandler.findByFilter(filter);
    }


    @GetMapping(path="/getItemForNewComing")
    public @ResponseBody
    DtoItemForNewComing getItemForNewComing(@RequestParam String filter) {
        return comingItemHandler.getItemForNewComing(filter);
    }

    // end c of coming API

}
