package barcode.dao.services;

import barcode.dao.entities.Buyer;
import barcode.dao.repositories.BuyerRepository;
import barcode.dto.DtoBuyer;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class BuyerHandler {

    private BuyerRepository buyerRepository;

    public BuyerHandler(BuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

    private ResponseItem<Buyer> update(Buyer newBuyer, Buyer buyer) {
        newBuyer.setName(buyer.getName());
        newBuyer.setDebt(buyer.getDebt());
        newBuyer.setAccount(buyer.getAccount());
        newBuyer.setBank(buyer.getBank());
        newBuyer.setUnp(buyer.getUnp());
        newBuyer.setAddress(buyer.getAddress());
        newBuyer.setDiscount(buyer.getDiscount());
        newBuyer.setSellByComingPrices(buyer.getSellByComingPrices() == null ? false : buyer.getSellByComingPrices());
        newBuyer.setLastPayDate(new Date());
        buyerRepository.save(newBuyer);
        return new ResponseItem<Buyer>("Покупатель добавлен/изменен ", true , newBuyer);
    }

    public ResponseItem<Buyer> addBuyer(Buyer buyer) {
        if(getBuyerByName(buyer.getName()) == null) {
            Buyer newBuyer = new Buyer();
            return update(newBuyer, buyer);
        }
        return new ResponseItem<Buyer>("Не создадим такого покупателя, уже есть в справочнике!", false);
    }

    public ResponseItem updateItem(Buyer buyer) {
        Buyer newBuyer =  buyerRepository.findOne(buyer.getId());
        return update(newBuyer, buyer);
    }

    public List<Buyer> getBuyers(String filter) {
        return this.buyerRepository
                .findByNameContainingIgnoreCaseOrderByIdAsc(filter);
    }

    public List<DtoBuyer> getDtoBuyers(String filter) {

        return buyerRepository.findByNameContainingIgnoreCaseOrderByIdAsc(filter)
                .stream()
                .map(b -> new DtoBuyer(b.getId(), b.getName()))
                .collect(Collectors.toList());
    }

    public Buyer getBuyerByName(String name) {
        List<Buyer> result = buyerRepository.findByNameIgnoreCase(name);
        if(result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public Buyer getBuyerById(Long id) {
        if(id > 0)
            return buyerRepository.findOne(id);
        else return null;
    }
}
