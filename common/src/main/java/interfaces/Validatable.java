package interfaces;

/**
 * Интерфейс для валидации данных объектов
 * Классы, реализующие этот интерфейс, должны предоставить метод проверки корректности своих данных
 */
public interface Validatable {
    /**
     * Проверяет корректность данных объекта
     * @throws IllegalArgumentException если данные некорректны
     */
    void validate() throws IllegalArgumentException;
} 