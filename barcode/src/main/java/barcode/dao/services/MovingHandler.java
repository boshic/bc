package barcode.dao.services;

import barcode.dao.entities.ComingItem;
import barcode.dao.entities.SoldItem;
import barcode.dao.entities.Stock;
import barcode.dao.entities.User;
import barcode.dto.ResponseItem;
import barcode.enums.CommentAction;
import barcode.utils.CommonUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class MovingHandler extends EntityHandlerImpl {

    private final ComingItemHandler comingItemHandler;

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

public ResponseItem makeMovings(Set<SoldItem> movings, Long stockId) {

        synchronized (comingItemHandler) {
            for(SoldItem moving : movings) {

                List<ComingItem> comings = this.comingItemHandler.getComingItemByIdAndStockId(
                        moving.getComing().getItem().getId(),
                        moving.getComing().getStock().getId());

                BigDecimal reqForMove = moving.getQuantity();

                BigDecimal availQuant;

                BigDecimal totalQuantity;

                BigDecimal availQuantityByEan = comingItemHandler.getAvailQuantityByEan(comings);

                if(reqForMove.compareTo(availQuantityByEan) > 0  || comings.size() == 0)
                    return new ResponseItem<SoldItem>(getInsufficientQuantityOfGoodsMessage(
                            reqForMove, availQuantityByEan, moving.getComing().getItem().getName()
                    ), false);

                for(ComingItem coming : comings) {

                    availQuant = coming.getCurrentQuantity();
                    totalQuantity = coming.getQuantity();

                    if(availQuant.compareTo(BigDecimal.ZERO) > 0) {

                        ComingItem newComing = new ComingItem();

//                    newComing.setComments(new ArrayList<>(coming.getComments()));
                        newComing.setComments(new ArrayList<>());

                        newComing.setComment(
                                this.buildComment(newComing.getComments(),
                                        "c " + coming.getStock().getName() +
                                                " на " + this.stockHandler.getStockById(stockId).getName() +
                                                getQuantityForComment(moving.getQuantity())
                                                + (CommonUtils.validateString(moving.getComment())),
                                        getCheckedUserName(moving.getUser()),
                                    CommentAction.MOVE_COMMENT.getAction(), newComing.getCurrentQuantity()));

                        newComing.setUser(moving.getUser());

                        newComing.setDate(new Date());

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

                            reqForMove = moving.getQuantity();

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
                                                    " на " + this.stockHandler.getStockById(stockId).getName() +
                                                    getQuantityForComment(moving.getQuantity())
                                                    + (CommonUtils.validateString(moving.getComment())),
                                            getCheckedUserName(moving.getUser()),
                                        CommentAction.MOVE_COMMENT.getAction(), coming.getCurrentQuantity()));

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
                                                    " на " + this.stockHandler.getStockById(stockId).getName()
                                                    + getQuantityForComment(moving.getQuantity())
                                                    + CommonUtils.validateString(moving.getComment()),
                                            getCheckedUserName(moving.getUser()),
                                        CommentAction.MOVE_COMMENT.getAction(), coming.getCurrentQuantity()));

                            comingItemHandler.saveComingItem(newComing);
                        }

                        if (reqForMove.compareTo(BigDecimal.ZERO) == 0) break;
                    }
                }
//
//                if(reqForMove.compareTo(BigDecimal.ZERO) > 0 || comings.size() == 0)
//                    return new ResponseItem(INSUFFICIENT_QUANTITY_OF_GOODS, false);
            }

            return new ResponseItem(MOVE_COMPLETED_SUCCESSFULLY, true);
        }

    }

    public ResponseItem makeAutoMoving (Set<SoldItem> movings) {
        ResponseItem<ResponseItem> responseItem =
                new ResponseItem<ResponseItem>("Обработка автоматических перемещений", new ArrayList<ResponseItem>(), true);

        ResponseItem responseItemTemp;

        Stock stockDest = new Stock();

        for (SoldItem moving : movings) {

            responseItemTemp = this.checkMoving(moving);

            responseItem.getEntityItems().add(responseItemTemp);

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

        responseItem.getEntityItems().add(responseByComing);

        if (!responseByComing.getSuccess())
            return responseByComing;

        moving.setComing(responseByComing.getEntityItem());

        moving.getComing().setStock(stock);

        Stock stockDest = this.stockHandler.getStockByName(moving.getBuyer().getName());

        if (stockDest == null)
            return new ResponseItem("Заведите склад-получатель со следующим наименованием! - " +
                                                                moving.getBuyer().getName(), false);


        responseItem.setSuccess(true);

        return responseItem;
    }

//    public synchronized ResponseItem addOneMoving(SoldItem moving, Long stockId) {
public ResponseItem addOneMoving(SoldItem moving, Long stockId) {

    synchronized(comingItemHandler) {

        ComingItem coming = comingItemHandler.getComingItemById(moving.getComing().getId());

        if(moving.getQuantity().compareTo(coming.getCurrentQuantity()) > 0)
            return new ResponseItem(getInsufficientQuantityOfGoodsMessage(
                    moving.getQuantity(), coming.getCurrentQuantity(), coming.getItem().getName()
            ), false);

        if(coming.getQuantity().compareTo(moving.getQuantity()) == 0) {
            coming.setComment(
                    this.buildComment(coming.getComments(),
                            "c " + coming.getStock().getName() +
                                    " на " + this.stockHandler.getStockById(stockId).getName()
                                    + getQuantityForComment(moving.getQuantity())
                                    + CommonUtils.validateString(moving.getComment()),
                            getCheckedUserName(moving.getUser()),
                        CommentAction.MOVE_COMMENT.getAction(), coming.getCurrentQuantity()));

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
            newComing.setComments(new ArrayList<>());
            coming.setComment(
                    this.buildComment(coming.getComments(),
                            "c " + coming.getStock().getName() +
                                    " на " + this.stockHandler.getStockById(stockId).getName()
                                    + getQuantityForComment(moving.getQuantity())
                                    + CommonUtils.validateString(moving.getComment()),
                            getCheckedUserName(moving.getUser()),
                        CommentAction.MOVE_COMMENT.getAction(), coming.getCurrentQuantity()));

            newComing.setComment(
                    this.buildComment(newComing.getComments(),
                            "c " + coming.getStock().getName() +
                                    " на " + this.stockHandler.getStockById(stockId).getName() +
                                    getQuantityForComment(moving.getQuantity())
                                    + CommonUtils.validateString(moving.getComment()),
                            getCheckedUserName(moving.getUser()),
                        CommentAction.MOVE_COMMENT.getAction(), newComing.getCurrentQuantity()));

            newComing.setStock(this.stockHandler.getStockById(stockId));
            newComing.setQuantity(moving.getQuantity());
            newComing.setCurrentQuantity(moving.getQuantity());
            newComing.setUser(moving.getUser());
            newComing.setDate(new Date());
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

        return new ResponseItem(MOVE_COMPLETED_SUCCESSFULLY, true);

    }

    }

}
