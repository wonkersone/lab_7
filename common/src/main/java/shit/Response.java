package shit;

import java.io.Serializable;

/**
 * Класс, представляющий ответ от сервера клиенту
 * Содержит информацию о типе ответа, сообщение и статус успешности операции
 */
public class Response implements Serializable {

    public enum ResponseType{
        INFO, NEED_WORKER, ERROR, ONE_MORE_SCRIPT, USER_AUTHORIZATION;
    }

    /** Тип ответа */
    public ResponseType type;
    /** Сообщение ответа */
    public String message;
    /** Флаг успешности операции */
    public boolean success;

    /**
     * Преобразует объект в строковое представление
     * Если сообщение длиннее 30 символов, оно обрезается и добавляется "..."
     * @return строковое представление ответа
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Ответ: \n");
        result.append("Тип ответа: ").append(type).append("\n");
        if (!message.isEmpty()) {
            result.append("Сообщение: ").append(message).append("\n");
        }
        result.append("Запрос выполнен успешно? ").append(success).append("\n");

        return result.toString();
    }

    /**
     * Конструктор без параметров
     */
    public Response() {}

    /**
     * Конструктор с типом и сообщением
     * @param type тип ответа
     * @param message сообщение
     */
    public Response(ResponseType type, String message) {
        this.type = type;
        this.message = message;
    }

    /**
     * Конструктор с типом, статусом успешности и сообщением
     * @param type тип ответа
     * @param success статус успешности
     * @param message сообщение
     */
    public Response(ResponseType type, boolean success, String message) {
        this.type = type;
        this.success = success;
        this.message = message;
    }

    /**
     * Конструктор со статусом успешности и сообщением
     * @param success статус успешности
     * @param message сообщение
     */
    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Response(boolean success) {
        this.success = success;
    }

    /**
     * Получает тип ответа
     * @return тип ответа
     */
    public ResponseType getType() { return type; }

    /**
     * Получает сообщение ответа
     * @return сообщение
     */
    public String getMessage() { return message; }

    /**
     * Проверяет успешность операции
     * @return true, если операция успешна, false в противном случае
     */
    public boolean isSuccess() { return success; }

    /**
     * Устанавливает тип ответа
     * @param type тип ответа
     */
    public void setType(ResponseType type) { this.type = type; }

    /**
     * Устанавливает сообщение ответа
     * @param message сообщение
     */
    public void setMessage(String message) { this.message = message; }

    /**
     * Устанавливает статус успешности операции
     * @param success статус успешности
     */
    public void setSuccess(boolean success) { this.success = success; }


}
