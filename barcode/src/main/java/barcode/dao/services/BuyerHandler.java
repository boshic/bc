package barcode.dao.services;

import barcode.dao.entities.Buyer;
import barcode.dao.entities.QBuyer;
import barcode.dao.predicates.BuyerPredicateBuilder;
import barcode.dao.repositories.BuyerRepository;
import barcode.dto.DtoBuyer;
import barcode.dto.ResponseItem;
import barcode.utils.CommonUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuyerHandler extends EntityHandlerImpl{

    private BuyerRepository buyerRepository;
    private BankHandler bankHandler;
    private AbstractEntityManager abstractEntityManager;

    public BuyerHandler(BuyerRepository buyerRepository,
                        BankHandler bankHandler,
                        AbstractEntityManager abstractEntityManager) {

        this.bankHandler = bankHandler;
        this.buyerRepository = buyerRepository;
        this.abstractEntityManager = abstractEntityManager;
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

    public List<DtoBuyer> getDtoBuyers(String filter) {

        QBuyer qBuyer = QBuyer.buyer;
        Predicate predicate = new BuyerPredicateBuilder().buildByFilter(filter);
        JPAQuery<Tuple> query = new JPAQuery<Tuple>(abstractEntityManager.getEntityManager())
            .select(qBuyer.id, qBuyer.name)
            .from(qBuyer)
            .where(predicate)
            .orderBy(qBuyer.sellings.size().desc());

        return query.fetch().stream()
            .map(b -> new DtoBuyer(b.get(0, Long.class), b.get(1, String.class)))
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
