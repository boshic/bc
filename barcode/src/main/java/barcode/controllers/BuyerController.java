package barcode.controllers;

import barcode.dao.entities.Buyer;
import barcode.dao.services.BuyerHandler;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path="/")
public class BuyerController {

    private BuyerHandler buyerHandler;

    public BuyerController(BuyerHandler buyerHandler) {
        this.buyerHandler = buyerHandler;
    }

    @RequestMapping(value = "/addBuyer", method = RequestMethod.POST)
    public @ResponseBody
    ResponseItem addBuyer (@RequestBody Buyer buyer) {
        if (buyer.getId() == null) {
            return this.buyerHandler.addBuyer(buyer);
        } else {
            return this.buyerHandler.updateItem(buyer);
        }
    }

    @GetMapping(path="/getBuyers")
    public @ResponseBody Iterable<Buyer> getBuyers(@RequestParam String filter) {
        return this.buyerHandler.getBuyers(filter);
    }

    @GetMapping(path="/getBuyerById")
    public @ResponseBody Buyer getItemById(@RequestParam Long id) {
        return this.buyerHandler.getBuyerById(id);
    }


}
