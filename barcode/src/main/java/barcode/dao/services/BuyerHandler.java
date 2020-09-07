package barcode.dao.services;

import barcode.api.EntityHandler;
import barcode.dao.entities.Buyer;
import barcode.dao.entities.QBuyer;
import barcode.dao.predicates.BuyerPredicateBuilder;
import barcode.dao.repositories.BuyerRepository;
import barcode.dto.DtoBuyer;
import barcode.dto.ResponseItem;
import barcode.utils.CommonUtils;
import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuyerHandler extends EntityHandlerImpl{

    private BuyerRepository buyerRepository;
    private BankHandler bankHandler;

    public BuyerHandler(BuyerRepository buyerRepository, BankHandler bankHandler) {

        this.bankHandler = bankHandler;
        this.buyerRepository = buyerRepository;
    }

    private ResponseItem<Buyer> update(Buyer newBuyer, Buyer buyer) {
        newBuyer.setName(buyer.getName());
        newBuyer.setDebt(CommonUtils.validateBigDecimal(buyer.getDebt()));
        newBuyer.setAccount(CommonUtils.validateString(buyer.getAccount()));
        newBuyer.setBank(bankHandler.getCheckedItem(buyer.getBank()));
        newBuyer.setUnp(CommonUtils.validateString(buyer.getUnp()));
        newBuyer.setAddress(buyer.getAddress());
        newBuyer.setDiscount(buyer.getDiscount() == null ? 0 : buyer.getDiscount());
        newBuyer.setSellByComingPrices(CommonUtils.validateBoolean(buyer.getSellByComingPrices()));
        newBuyer.setExcludeExpensesFromIncome(CommonUtils.validateBoolean(buyer.getExcludeExpensesFromIncome()));
//        newBuyer.setUseForInventory(checkAndGetInventorySign(buyer.getUseForInventory()));
        newBuyer.setLastPayDate(new Date());
        buyerRepository.save(newBuyer);
        return new ResponseItem<Buyer>("Покупатель добавлен/изменен ", true , newBuyer);
    }

    private synchronized Boolean checkAndGetInventorySign(Boolean useForInventory) {

        return checkAndGetInventorySignForEntity(getBuyerForInventory(), useForInventory );
    }

    private Buyer getBuyerForInventory() {

        return getEntityForInventory(buyerRepository, new BooleanBuilder().and(QBuyer.buyer.useForInventory.isTrue()));
    }

    public ResponseItem<Buyer> addBuyer(Buyer buyer) {
        if(getBuyerByName(buyer.getName()) == null) {
            Buyer newBuyer = new Buyer();
            return update(newBuyer, buyer);
        }

        return setToNameIncorrectEntityFields(buyer, stringsToList(ENTITY_NAME));
    }

    public ResponseItem updateItem(Buyer buyer) {
        Buyer newBuyer =  buyerRepository.findOne(buyer.getId());
        return update(newBuyer, buyer);
    }

    public List<Buyer> getBuyers(String filter) {

        Sort sort = new Sort(Sort.Direction.ASC, "name");

        return this.buyerRepository.findAll(new BuyerPredicateBuilder().buildByFilter(filter), sort);
    }

    public List<DtoBuyer> getDtoBuyers(String filter) {

        if (filter.equals(""))
        return buyerRepository.getBuyersOrderedBySellingsSize()
                .stream()
                .map(b -> new DtoBuyer(b.getId(), b.getName()))
                .collect(Collectors.toList());

        Sort sort = new Sort(Sort.Direction.ASC, "id");

        List<Buyer> buyers = buyerRepository.findAll(new BuyerPredicateBuilder().buildByFilter(filter), sort);

        if(buyers.size() > 20)
            return buyers
                    .stream()
                    .map(b -> new DtoBuyer(b.getId(), b.getName()))
                    .collect(Collectors.toList());

        return buyers
                .stream()
                .sorted(Comparator.comparing(Buyer::getSellings, (s1, s2) -> {
                    return  s2.size() - s1.size();
                }))
                .map(b -> new DtoBuyer(b.getId(), b.getName()))
                .collect(Collectors.toList());
    }

    Buyer getBuyerByName(String name) {
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
