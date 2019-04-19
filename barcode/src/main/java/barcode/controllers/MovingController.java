package barcode.controllers;

import barcode.dao.entities.SoldItem;
import barcode.dao.services.MovingHandler;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequestMapping(path="/")
public class MovingController {

    private MovingHandler movingHandler;
    public MovingController(MovingHandler movingHandler) {
        this.movingHandler = movingHandler;
    }

    @RequestMapping(value = "/makeMovings", method = RequestMethod.POST)
    public @ResponseBody
    ResponseItem makeMovings(@RequestBody Set<SoldItem> movings, @RequestParam Long stockId) {
        return this.movingHandler.makeMovings(movings, stockId);
    }

    @RequestMapping(value = "/makeAutoMovings", method = RequestMethod.POST)
    public @ResponseBody
    ResponseItem makeAutoMovings(@RequestBody Set<SoldItem> movings) {
        return this.movingHandler.makeAutoMoving(movings);
    }

    @RequestMapping(value="/addOneMoving", method = RequestMethod.POST)
    public @ResponseBody
    ResponseItem addOneMoving(@RequestBody SoldItem soldItem, @RequestParam Long stockId) {

        return this.movingHandler.addOneMoving(soldItem, stockId);
    }

}
