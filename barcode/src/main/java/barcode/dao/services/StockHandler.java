package barcode.dao.services;

import barcode.dao.entities.embeddable.AllowedStock;
import barcode.dao.entities.Stock;
import barcode.dao.entities.User;
import barcode.dao.repositories.StockRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockHandler {

    private StockRepository stockRepository;
    private UserHandler userHandler;

    public StockHandler(StockRepository stockRepository, UserHandler userHandler) {

        this.userHandler = userHandler;

        this.stockRepository = stockRepository;
    }

    public Iterable<Stock> getStocks(Boolean allowAll) {

        User user = userHandler.getCurrentUser();

        List<Stock> stocks = new ArrayList<Stock>();

        if(user.getStocksAllowed().size() == 0)
             stocks = (allowAll) ? stockRepository.findAll()
                                        : stockRepository.findByAllowAll(allowAll);

        else
            for(AllowedStock allowedStock: user.getStocksAllowed()) {

                if(!allowAll && allowedStock.getStock().isAllowAll())
                    continue;

                    stocks.add(allowedStock.getStock());
            }

        for(Stock stock: stocks)
            if(stock.getId().equals(user.getStock().getId()))
                stock.setSelected(true);

        return stocks;
    }

    public Stock getStockByName(String name) {
        return stockRepository.findByName(name);
    }

    public Stock getStockById(Long id) { return stockRepository.findOne(id); }
}
