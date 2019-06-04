package barcode.dao.services;

import barcode.dao.entities.ComingItem;
import barcode.dao.entities.SoldItem;
import barcode.dao.entities.Stock;
import barcode.dao.entities.User;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class MovingHandler extends EntityHandlerImpl {

    private static final String AUTO_MOVING_MAKER = "Автоперемещение";

    private static final String MOVING_ACTION  = "Перемещение";

    private ComingItemHandler comingItemHandler;

    private StockHandler stockHandler;

    private UserHandler userHandler;

    public MovingHandler(ComingItemHandler comingItemHandler, StockHandler stockHandler,
                                                                     UserHandler userHandler) {

        this.comingItemHandler = comingItemHandler;

        this.stockHandler = stockHandler;

        this.userHandler = userHandler;

    }

    private String getCheckedUserName(User user) {
        return userHandler.checkUser(user, AUTO_MOVING_MAKER).getFullName();
    }

    public synchronized ResponseItem makeMovings(Set<SoldItem> movings, Long stockId) {

        for(SoldItem moving : movings) {

            List<ComingItem> comings = this.comingItemHandler.getComingItemByIdAndStockId(
                                    moving.getComing().getItem().getId(),
                                    moving.getComing().getStock().getId());

            BigDecimal reqForMove = BigDecimal.ZERO;

            BigDecimal availQuant;

            BigDecimal totalQuantity;

            for(ComingItem coming : comings) {

                availQuant = coming.getCurrentQuantity();

                reqForMove = moving.getQuantity();

                totalQuantity = coming.getQuantity();

                if(availQuant.compareTo(BigDecimal.ZERO) > 0) {

                    ComingItem newComing = new ComingItem();

//                    newComing.setComments(new ArrayList<>(coming.getComments()));
                    newComing.setComments(new ArrayList<>());
//
//                    newComing.setComment(
//                            this.buildComment(newComing.getComments(),
//                                    "на " + this.stockHandler.getStockById(stockId).getName() + " "
//                                            + moving.getQuantity() + " ед., " + moving.getComment(),
//                                    getCheckedUserName(moving.getUser()),
//                                    MOVING_ACTION));


                    newComing.setComment(
                            this.buildComment(newComing.getComments(),
                                    "c " + coming.getStock().getName() +
                                            " на " + this.stockHandler.getStockById(stockId).getName() + " "
                                            + moving.getQuantity() + " ед., " + moving.getComment(),
                                    getCheckedUserName(moving.getUser()),
                                    MOVING_ACTION));

                    newComing.setUser(moving.getUser());

                    newComing.setFactDate(new Date());

                    newComing.setLastChangeDate(new Date());

                    newComing.setPriceIn(coming.getPriceIn());

                    newComing.setPriceOut(moving.getPrice());

                    newComing.setDoc(coming.getDoc());

                    newComing.setItem(coming.getItem());

                    newComing.setStock(this.stockHandler.getStockById(stockId));

                    coming.setLastChangeDate(new Date());

                    if (reqForMove.compareTo(availQuant) > 0) {

                        moving.setQuantity(reqForMove.subtract(availQuant));

                        newComing.setQuantity(availQuant);

                        newComing.setCurrentQuantity(availQuant);

                        coming.setCurrentQuantity(BigDecimal.ZERO);

                        coming.setQuantity(totalQuantity.subtract(availQuant));

                    } else {

                        newComing.setQuantity(reqForMove);

                        newComing.setCurrentQuantity(reqForMove);

                        coming.setCurrentQuantity(availQuant.subtract(reqForMove));

                        coming.setQuantity(totalQuantity.subtract(reqForMove));

                        reqForMove = BigDecimal.ZERO;

                    }

                    coming.setSum(coming.getPriceIn().multiply(coming.getCurrentQuantity())
                            .setScale(2, BigDecimal.ROUND_HALF_UP));

                    newComing.setSum(newComing.getPriceIn().multiply(newComing.getCurrentQuantity())
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));

                    if(coming.getQuantity().compareTo(BigDecimal.ZERO) == 0) {

                        coming.setComment(
                                this.buildComment(coming.getComments(),
                                        "c " + coming.getStock().getName() +
                                                " на " + this.stockHandler.getStockById(stockId).getName() + " "
                                                + moving.getQuantity() + " ед., " + moving.getComment(),
                                        getCheckedUserName(moving.getUser()),
                                        MOVING_ACTION));
//
//                        coming.setComment(
//                                this.buildComment(coming.getComments(),
//                                        "на " + this.stockHandler.getStockById(stockId).getName() + " "
//                                                + moving.getQuantity() + " ед., " + moving.getComment(),
//                                        getCheckedUserName(moving.getUser()),
//                                        MOVING_ACTION));

                        coming.setStock(this.stockHandler.getStockById(stockId));

                        coming.setQuantity(newComing.getQuantity());

                        coming.setCurrentQuantity(newComing.getCurrentQuantity());

                        coming.setUser(newComing.getUser());

                        coming.setPriceOut(newComing.getPriceOut());

                        coming.setSum(newComing.getSum());

                        comingItemHandler.saveComingItem(coming);

                    } else {

                        coming.setComment(
                                this.buildComment(coming.getComments(),
                                        "c " + coming.getStock().getName() +
                                                " на " + this.stockHandler.getStockById(stockId).getName() + " "
                                                + moving.getQuantity() + " ед., " + moving.getComment(),
                                        getCheckedUserName(moving.getUser()),
                                        MOVING_ACTION));

                        comingItemHandler.saveComingItem(newComing);
                    }

                    if (reqForMove.compareTo(BigDecimal.ZERO) == 0) break;

                }
            }

            if(reqForMove.compareTo(BigDecimal.ZERO) > 0)
                return new ResponseItem("увы недостаточно для перемещения... не хватает - " + reqForMove, false);
        }

        return new ResponseItem("Перемещения выполнены", true);

    }

    public ResponseItem makeAutoMoving (Set<SoldItem> movings) {
        ResponseItem<ResponseItem> responseItem =
                new ResponseItem<ResponseItem>("Обработка автоматических перемещений", new ArrayList<ResponseItem>(), true);

        ResponseItem responseItemTemp;

        Stock stockDest = new Stock();

        for (SoldItem moving : movings) {

            responseItemTemp = this.checkMoving(moving);

            responseItem.getItems().add(responseItemTemp);

            if (!responseItemTemp.getSuccess()) {

                responseItem.setText("Перемещение не состоится");

                return responseItem;
            }

            if(stockDest.getId() == null)
                stockDest = stockHandler.getStockByName(moving.getBuyer().getName());
        }

            this.makeMovings(movings, stockDest.getId());

        return responseItem;
    }


    private ResponseItem checkMoving(SoldItem moving) {

        ResponseItem<ResponseItem> responseItem = new ResponseItem<ResponseItem>("результат проверки прихода товара "
                + moving.getComing().getItem().getName(), new ArrayList<ResponseItem>(), false);

//        user
        User user = userHandler.getUserByName(moving.getUser().getName());

        if (user == null || !user.getName().equals("Автоперемещение"))
            return new ResponseItem("Пользователь не найден в БД" , false);

        moving.setUser(user);

        //stock
        Stock stock = stockHandler.getStockByName(moving.getComing().getStock().getName());

        if (stock == null)
            return new ResponseItem("Заведите склад со следующим наименованием! - " +
                                                            moving.getComing().getStock().getName(), false);


        //coming
        ResponseItem<ComingItem> responseByComing = this.comingItemHandler.getComingForSell(
                                                        moving.getComing().getItem().getEan(), stock.getId());

        responseItem.getItems().add(responseByComing);

        if (!responseByComing.getSuccess())
            return responseByComing;

        moving.setComing(responseByComing.getItem());

        moving.getComing().setStock(stock);

        Stock stockDest = this.stockHandler.getStockByName(moving.getBuyer().getName());

        if (stockDest == null)
            return new ResponseItem("Заведите склад-получатель со следующим наименованием! - " +
                                                                moving.getBuyer().getName(), false);


        responseItem.setSuccess(true);

        return responseItem;
    }

    public synchronized ResponseItem addOneMoving(SoldItem moving, Long stockId) {

        ComingItem coming = comingItemHandler.getComingItemById(moving.getComing().getId());

        if(coming.getQuantity().compareTo(moving.getQuantity()) == 0) {

            coming.setComment(
                    this.buildComment(coming.getComments(),
                            "c " + coming.getStock().getName() +
                            " на " + this.stockHandler.getStockById(stockId).getName() + " "
                                    + moving.getQuantity() + " ед., " + moving.getComment(),
                            getCheckedUserName(moving.getUser()),
                            MOVING_ACTION));

            coming.setStock(this.stockHandler.getStockById(stockId));

            coming.setQuantity(moving.getQuantity());

            coming.setCurrentQuantity(moving.getQuantity());

            coming.setUser(moving.getUser());

            coming.setPriceOut(moving.getPrice());

            coming.setSum(coming.getPriceIn().multiply(coming.getCurrentQuantity())
                    .setScale(2, BigDecimal.ROUND_HALF_UP));

            comingItemHandler.saveComingItem(coming);

        } else {

            ComingItem newComing = new ComingItem();

//            newComing.setComments(new ArrayList<>(coming.getComments()));

            newComing.setComments(new ArrayList<>());
            coming.setComment(
                    this.buildComment(coming.getComments(),
                            "c " + coming.getStock().getName() +
                                    " на " + this.stockHandler.getStockById(stockId).getName() + " "
                                    + moving.getQuantity() + " ед., " + moving.getComment(),
                            getCheckedUserName(moving.getUser()),
                            MOVING_ACTION));

            newComing.setComment(
                    this.buildComment(newComing.getComments(),
                            "c " + coming.getStock().getName() +
                            " на " + this.stockHandler.getStockById(stockId).getName() + " "
                                    + moving.getQuantity() + " ед., " + moving.getComment(),
                            getCheckedUserName(moving.getUser()),
                            MOVING_ACTION));

            newComing.setStock(this.stockHandler.getStockById(stockId));

            newComing.setQuantity(moving.getQuantity());

            newComing.setCurrentQuantity(moving.getQuantity());

            newComing.setUser(moving.getUser());

            newComing.setFactDate(new Date());

            newComing.setLastChangeDate(new Date());

            newComing.setPriceIn(coming.getPriceIn());

            newComing.setPriceOut(moving.getPrice());

            newComing.setDoc(coming.getDoc());

            newComing.setItem(coming.getItem());

            coming.setQuantity(coming.getQuantity().subtract(moving.getQuantity()));

            coming.setCurrentQuantity(coming.getCurrentQuantity().subtract(moving.getQuantity()));

            coming.setLastChangeDate(new Date());

            coming.setSum(coming.getPriceIn().multiply(coming.getCurrentQuantity())
                    .setScale(2, BigDecimal.ROUND_HALF_UP));

            newComing.setSum(newComing.getPriceIn().multiply(newComing.getCurrentQuantity())
                    .setScale(2, BigDecimal.ROUND_HALF_UP));

            comingItemHandler.saveComingItem(newComing);

        }

        return new ResponseItem("Перемещено", true);
    }

}
