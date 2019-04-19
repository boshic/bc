package barcode.controllers;


import barcode.dao.entities.Stock;
import barcode.dao.services.StockHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/")
public class StockController {

    private StockHandler stockHandler;

    public StockController(StockHandler stockHandler) {
        this.stockHandler = stockHandler;
    }

    @GetMapping(path="/getStocks")
    public @ResponseBody
    Iterable<Stock> getStocks(@RequestParam Boolean allowAll) {
        return this.stockHandler.getStocks(allowAll);
    }

}
