package barcode.dao.services;


import barcode.api.EntityHandler;
import barcode.dao.entities.basic.BasicCounterPartyEntity;
import barcode.dao.entities.basic.BasicOperationWithCommentEntity;
import barcode.dao.entities.embeddable.Comment;

import barcode.dao.utils.BasicFilter;
import barcode.dao.utils.SortingStrategy;
import com.querydsl.core.types.Predicate;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xlinux on 30.07.18.
 */
public class EntityHandlerImpl implements EntityHandler{

    private static final Integer MAX_COMMENT_LENGTH = 2000;
    static final Integer EAN_LENGTH = 13;

    private static final String COMMENT_TOO_LONG = "";
    private static final String FAILED = "Неудачно! ";
    static final String SMTH_FOUND = " найден(а)";
    static final String SMTH_CREATED = " создан(а)";

    static final String INVENTORY_DOC_NAME = "Инвентаризация";
    static final String INVENTORY_DOC_CREATED = "Инвентаризация";
    static final String INVENTORY_SURPLUS_DETECTED = "Излишки при инвентаризации";
    static final String INVENTORY_SHORTAGE_DETECTED = "Недостача при инвентаризации";
    static final String INVENTORY_DONE = "результаты инвентаризации применены!";
    static final String BUYER_FOR_INVENTORY_NOT_FOUND = FAILED +
            "Не задан покупатель для списания недостачи!";
    static final String SUPPLIER_FOR_INVENTORY_NOT_FOUND = FAILED +
            "Не задан поставщик для документа инвентаризации";

    static final String BAD_EAN_SYNONYM = FAILED +
            "Товар имеет приходы, или указанный ШК уже имеет синоним!";
    static final String ITEM_ALREADY_EXIST = FAILED +
            "С заданным ШК товар уже существует, добавление не состоится";

    static final String NEW_INVOICE_ADDED = "добавлен отчет";
    static final String INVOICE_CHANGED = "добавлен/изменен";
    static final String WRITE_OFF_CAUSE = "причина списания";
    static final String WRITE_OFF_ACT_ADDED = "акт на списание";

    static final String AUTO_COMING_MAKER = "Автоприход";
    static final String AUTO_MOVING_MAKER = "Автоперемещение";
    static final String CHECK_COMING_INVALID_DOC = FAILED +
            "Приход с указанным товаром, ценой и документом уже содержится! ";
    static final String ITEM_IS_COMPOSITE_ERROR= FAILED +
            "Для компонентных товаров приход создать нельзя! ";
    static final String TRY_TO_CHANGE_SOLD_COMING_ERROR= FAILED +
            "Нельзя изменить приход, который уже продавался! ";
    static final String INSUFFICIENT_QUANTITY_OF_GOODS = FAILED + "Не хватает количества товара! ";

    static final String MAKING_OF_COMING= "Оприходование ";
    static final String CHANGING_OF_COMING= "Изменение прихода ";
    static final String ELEMENTS_FOUND = "найдены элементы";

    static final String SALE_COMPLETED_SUCCESSFULLY = "Продажа завершена успешно";
    static final String MOVE_COMPLETED_SUCCESSFULLY = "Перемещение завершено успешно";
    static final String DATE_CHANGED = "Изменена дата";
    static final String SALE_COMMENT = "Продажа";
    static final String MOVE_COMMENT  = "Перемещение";
    static final String RETURN_COMMENT = "Возврат";
    static final String COMMON_UNIT = " ед. ";
    static final String NOTHING_FOUND = "ничего не найдено";
    static final SimpleDateFormat DATE_FORMAT_WITH_TIME = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    static final SimpleDateFormat DATE_FORMAT_WO_TIME = new SimpleDateFormat("dd.MM.yyyy");
    static final String SEPARATOR = "; ";
    static final String SPACE = " ";


    static final String DEAFULT_SECTION_NAME = "Секция не задана ";
    static final String CHECK_ITEM_LOG_MESS = "Товар с именем ";
    static final String CHECK_SECTION_LOG_MESS = "Секция с именем ";

    public String generateComment(String oldComment, String user, String action) {

//        (oldComment == null || oldComment.equals("")) ? "" :
//                (oldComment.length() > 1 && oldComment.substring(oldComment.length() - 2).equals(SEPARATOR)) ?
//                        oldComment :
//                        oldComment + SEPARATOR
//

        return user + " " +DATE_FORMAT_WITH_TIME.format(new Date()) + " "
                + action + SEPARATOR +
                ((oldComment == null || oldComment.equals("")) ? "" : oldComment + SPACE);
    }

    public String buildComment(List<Comment> comments, String text, String user, String action, BigDecimal quantity) {
        if(user.length() > 0)
            comments.add(new Comment((text == null ? "": text), user, action, new Date(), quantity));
        String comment = "";
        comments.sort(Comparator.comparing(Comment::getDate));
        for (Comment c: comments)
            comment += c.getAction() + SPACE + c.getText() + SPACE + c.getUserName() +
                    SPACE + DATE_FORMAT_WITH_TIME.format(c.getDate())+SEPARATOR;

        return comment.length() > MAX_COMMENT_LENGTH ? COMMENT_TOO_LONG : comment;
    }

    String getQuantityForComment (BigDecimal quantity) {

        BigDecimal fraction = quantity.remainder(BigDecimal.ONE);

        return " " + (fraction.compareTo(BigDecimal.ZERO) > 0 ? quantity.toString() : quantity.toBigInteger().toString()) + COMMON_UNIT;
    }

    public String getCommentByAction(List<Comment> comments, String action) {

        String result = "";
        for(Comment comment: comments)
            if(comment.getAction().equalsIgnoreCase(action))
                result += comment.getText() + SEPARATOR;

        return result;
    }


    public Date getDateOnly(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 3);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public Date getDateByTime(final Date date, Integer hour, Integer minute) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    String getInsufficientQuantityOfGoodsMessage(BigDecimal reqQuant, BigDecimal availQuant, String itemName) {
        return  INSUFFICIENT_QUANTITY_OF_GOODS + reqQuant + " из " + availQuant + COMMON_UNIT + itemName;
    }


    synchronized <T extends BasicOperationWithCommentEntity>
    void changeDate(Long id, Date newDate, CrudRepository<T, Long> repository, String username) {

        T item = repository.findOne(id);

        item.setComment(this.buildComment(item.getComments(), "", username, DATE_CHANGED, BigDecimal.ZERO));

        item.setDate(newDate);

        repository.save(item);
    }

    <T extends BasicCounterPartyEntity> T getEntityForInventory(QueryDslPredicateExecutor<T> repository, Predicate predicate) {

        return repository.findOne(predicate);
    }


    <T extends BasicCounterPartyEntity> Boolean
    checkAndGetInventorySignForEntity(T item, Boolean useForInventory) {

        if(useForInventory == null)
            return false;

        if(useForInventory) {
            if(item != null)
                item.setUseForInventory(false);
            return true;
        }

        return useForInventory;
    }

    <T> void sortGroupedItems(List<T> items,
                                    String direction,
                                    SortingStrategy<T> strategy) {
        strategy.sort(items);

        if(direction.equalsIgnoreCase(BasicFilter.SORT_DIRECTION_DESC))
            Collections.reverse(items);
    }

}
