package barcode.enums;

public enum SystemMessage {

    NEW_REPORT_ADDED("Добавлен отчет"),
    MAKING_OF_COMING ("Оприходование "),
    CHANGING_OF_COMING ("Изменение прихода"),
    SMTH_DELETED("удален(а) ");

    private String message;

    SystemMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
