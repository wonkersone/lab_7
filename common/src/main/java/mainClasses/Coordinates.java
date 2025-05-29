package mainClasses;

import interfaces.Validatable;

import java.io.Serializable;

/**
 * Класс, представляющий координаты в двумерном пространстве
 * Содержит координаты x и y
 */
public class Coordinates implements Validatable, Serializable {
    private Double x; //Максимальное значение поля: 100
    private long y; //Поле не может быть null, Значение поля должно быть больше -415

    /**
     * Конструктор без параметров
     */
    public Coordinates() {}

    /**
     * Конструктор с параметрами
     * @param x координата x (максимальное значение: 100)
     * @param y координата y (должна быть больше -415)
     */
    public Coordinates(Double x, long y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Получает координату x
     * @return координата x
     */
    public Double getX() {
        return x;
    }

    /**
     * Получает координату y
     * @return координата y
     */
    public long getY() {
        return y;
    }

    /**
     * Устанавливает координату x
     * @param x координата x (максимальное значение: 100)
     */
    public void setX(Double x) {
        this.x = x;
    }

    /**
     * Устанавливает координату y
     * @param y координата y (должна быть больше -415)
     */
    public void setY(long y) {
        this.y = y;
    }

    /**
     * Преобразует объект в строковое представление
     * @return строковое представление координат в формате "(x, y)"
     */
    @Override
    public String toString() {
        return "(x, y) : (" + x + ", " + y + ")";
    }

    /**
     * Сравнивает текущий объект с другим объектом
     * @param o объект для сравнения
     * @return true, если объекты равны, false в противном случае
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return y == that.y && x.equals(that.x);
    }

    /**
     * Вычисляет хеш-код объекта
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return x.hashCode() + Long.hashCode(y);
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (x == null) {
            throw new IllegalArgumentException("Координата X не может быть null");
        }
        if (y <= -415) {
            throw new IllegalArgumentException("Координата Y должна быть больше -415");
        }
    }
}