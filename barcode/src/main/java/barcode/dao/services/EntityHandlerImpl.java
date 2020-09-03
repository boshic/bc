package barcode.dao.services;


import barcode.api.EntityHandler;
import barcode.dao.entities.basic.BasicCounterPartyEntity;
import barcode.dao.entities.basic.BasicNamedEntity;
import barcode.dao.entities.basic.BasicOperationWithCommentEntity;
import barcode.dao.entities.embeddable.Comment;

import barcode.utils.BasicFilter;
import barcode.utils.SortingStrategy;
import barcode.dto.ResponseItem;
import barcode.enums.CommentAction;
import com.querydsl.core.types.Predicate;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xlinux on 30.07.18.
 */
public class EntityHandlerImpl implements EntityHandler {

    private static final Integer MAX_COMMENT_LENGTH = 2000;
    static final Integer EAN_LENGTH = 13;

    private static final String COMMENT_TOO_LONG = "";
    private static final String FAILED = "Неудачно! ";
    private static final String SUCCESSFULLY = " успешно! ";

    static final String SMTH_FOUND = " найден(а)";
    static final String SMTH_CREATED = " создан(а)";

    static final String NUMBER = "номер ";

    static final String ROLE_ADMIN = "ROLE_ADMIN";

    static final String ENTITY_NAME = "Наименование";
    static final String ENTITY_DATE = "Дата";
    static final String ENTITY_BANK_CODE = "БИК";

    static final String INVENTORY_DOC_NAME = "Инвентаризация";

    static final String INVENTORY_DONE = "результаты инвентаризации применены!";
    static final String BUYER_FOR_INVENTORY_NOT_FOUND = FAILED +
            "Не задан покупатель для списания недостачи!";
    static final String INCORRECT_ENTITY_FIELD = FAILED +
            "Элемент с указанным реквизитом уже содержится в справочнике!";
    static final String SUPPLIER_FOR_INVENTORY_NOT_FOUND = FAILED +
            "Не задан поставщик для документа инвентаризации";

    static final String BAD_EAN_SYNONYM = FAILED +
            "Товар имеет приходы, или указанный ШК уже имеет синоним!";
    static final String ITEM_ALREADY_EXIST = FAILED +
            "С заданным ШК товар уже существует, добавление не состоится";

    static final String ITEMS_FOUND = "Товар найден в остатках в количестве: ";
    static final String ITEM_NOT_FOUND_WITH_SUCH_EAN = FAILED + "Не найден товар с заданным ШК: ";
    static final String NOT_ENOUGH_ITEMS = FAILED + "Товара нет в наличии!";
    static final String COMPOSITE_ITEMS_CASTING_IS_NOT_ALLOWED = FAILED + "Запрещен подбор компонентных товаров : ";
    static final String DELETING_FAILED = FAILED + "Удаление не удалось!";
    static final String CHANGING_DENIED = FAILED + "Нет прав для изменения/ввода данных!";

    static final String NEW_REPORT_ADDING_FAILED = FAILED + "Не удалось добавить отчет!";

    static final String WRITE_OFF_CAUSE = "причина списания";

    static final String AUTO_COMING_MAKER = "Автоприход";
    static final String AUTO_MOVING_MAKER = "Автоперемещение";
    static final String CHECK_COMING_INVALID_DOC = FAILED +
            "Приход с указанным товаром, ценой и документом уже содержится! ";
    static final String ITEM_IS_COMPOSITE_ERROR= FAILED +
            "Для компонентных товаров приход создать нельзя! ";
    static final String TRY_TO_CHANGE_SOLD_COMING_ERROR= FAILED +
            "Нельзя изменить приход, который уже продавался! ";
    static final String INSUFFICIENT_QUANTITY_OF_GOODS = FAILED + "Не хватает количества товара! ";

    static final String ELEMENTS_FOUND = "найдены элементы";

    static final String SALE_COMPLETED_SUCCESSFULLY = "Продажа завершена" + SUCCESSFULLY;
    static final String RETURN_COMPLETED_SUCCESSFULLY = "Возврат завершен" + SUCCESSFULLY;
    static final String MOVE_COMPLETED_SUCCESSFULLY = "Перемещение завершено" + SUCCESSFULLY;

    static final String COMMON_UNIT = " ед. ";
    static final String NOTHING_FOUND = "ничего не найдено";
    static final SimpleDateFormat DATE_FORMAT_WITH_TIME = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    static final SimpleDateFormat DATE_FORMAT_WO_TIME = new SimpleDateFormat("dd.MM.yyyy");
    static final String SEPARATOR = "; ";
    static final String SPACE = " ";


    static final String DEAFULT_SECTION_NAME = "Секция не задана ";
    static final String CHECK_ITEM_LOG_MESS = "Товар с именем ";
    static final String CHECK_SECTION_LOG_MESS = "Секция с именем ";

    String generateCommentSearchString(String action, String text, String userName, Date date) {

        return action + SPACE + text + SPACE + userName + SPACE +  DATE_FORMAT_WITH_TIME.format(date)+SEPARATOR;
    }

    public String buildComment(List<Comment> comments, String text, String user, String action, BigDecimal quantity) {
        if(user.length() > 0)
            comments.add(new Comment(
                    (text == null ? "": text),
                    user,
                    action,
                    generateCommentSearchString(action, text, user, new Date()),
                    new Date(),
                    quantity));

        String comment = "";
        comments.sort(Comparator.comparing(Comment::getDate));
        for (Comment c: comments)
            comment += generateCommentSearchString(c.getAction(), c.getText(), c.getUserName(), c.getDate());

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

    private String getIncorrectEntityFieldFoundMessage(List<String> fields) {
        String value = "";
        for(String f : fields)
            value = value + f + ", ";

        return "Элемент с указанными реквизитами: " + value + " уже содержится в справочнике";
    }


    synchronized <T extends BasicOperationWithCommentEntity>
    void changeDate(Long id, Date newDate, CrudRepository<T, Long> repository, String username) {

        T item = repository.findOne(id);

        item.setComment(this.buildComment(item.getComments(),
            "",
            username,
            CommentAction.DATE_CHANGED.getAction(), BigDecimal.ZERO));

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

//    <T> void sortGroupedItems(List<T> items,
//                              String direction,
//                              SortingStrategy<T> strategy) {
//        strategy.sort(items);
//        if(direction.equalsIgnoreCase(BasicFilter.SORT_DIRECTION_DESC))
//            Collections.reverse(items);
//    }

    <T extends BasicNamedEntity> ResponseItem<T> setToNameIncorrectEntityFields(T item, List<String> fields) {
        String message = getIncorrectEntityFieldFoundMessage(fields);
        item.setName(message);
        return new ResponseItem<T>(message, false, item);
    }

    List<String> stringsToList(String ... fields) {

        return Arrays.asList(fields);
    }

}
