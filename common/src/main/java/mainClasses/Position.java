package mainClasses;

import java.io.Serializable;

/**
 * Перечисление, представляющее должности работников
 */
public enum Position implements Serializable {
    BAKER("Пекарь"), CLEANER("Уборщик"), MANAGER_OF_CLEANING("Менеджер по уборке");

    private final String value;

    /**
     * Конструктор перечисления
     * @param value строковое представление должности
     */
    Position(String value) {
        this.value = value;
    }

    /**
     * Получает строковое представление должности
     * @return строковое представление должности на русском языке
     */
    public String getValue() {
        return value;
    }
}