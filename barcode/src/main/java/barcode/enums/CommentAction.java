package barcode.enums;

public enum CommentAction {

    DATE_CHANGED ("Изменена дата"),

    INVENTORY_SURPLUS_DETECTED ("Излишки при инвентаризации"),
    INVENTORY_SHORTAGE_DETECTED("Недостача при инвентаризации"),

    SMTH_DELETED(SystemMessage.SMTH_DELETED.getMessage()),
    SMTH_CHANGED("добавлен(а)/изменен(а)"),

    SALE_COMMENT("Продажа"),
    DELETED_SALE_COMMENT("Удаление в режиме продажи"),
    MOVE_COMMENT("Перемещение"),
    WRITE_OFF_CAUSE("Причина списания"),
    RETURN_COMMENT("Возврат"),

    WRITE_OFF_ACT_ADDED("акт на списание"),
    NEW_REPORT_ADDED(SystemMessage.NEW_REPORT_ADDED.getMessage()),

    MAKING_OF_COMING (SystemMessage.MAKING_OF_COMING.getMessage()),
    CHANGING_OF_COMING (SystemMessage.CHANGING_OF_COMING.getMessage());

    private String action;

    CommentAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action.trim();
    }
}
