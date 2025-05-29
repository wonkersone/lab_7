package mainClasses;

import interfaces.Validatable;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Класс, представляющий информацию о человеке
 * Содержит основные характеристики: дату рождения, рост и вес
 */
public class Person implements Validatable , Serializable {
    private Date birthday; //Поле может быть null
    private Float height; //Поле не может быть null, Значение поля должно быть больше 0
    private Float weight; //Значение поля должно быть больше 0

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Конструктор без параметров
     */
    public Person() {}

    /**
     * Конструктор с параметрами
     * @param birthday дата рождения человека
     * @param height рост человека
     * @param weight вес человека
     */
    public Person(Date birthday, Float height, Float weight) {
        this.birthday = birthday;
        this.height = height;
        this.weight = weight;
    }

    /**
     * Получает дату рождения человека
     * @return дата рождения
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * Получает рост человека
     * @return рост
     */
    public Float getHeight() {
        return height;
    }

    /**
     * Получает вес человека
     * @return вес
     */
    public Float getWeight() {
        return weight;
    }

    /**
     * Устанавливает дату рождения человека
     * @param birthday дата рождения
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * Устанавливает рост человека
     * @param height рост
     */
    public void setHeight(Float height) {
        this.height = height;
    }

    /**
     * Устанавливает вес человека
     * @param weight вес
     */
    public void setWeight(Float weight) {
        this.weight = weight;
    }

    /**
     * Преобразует объект в строковое представление
     * @return строковое представление объекта Person
     */
    @Override
    public String toString() {
        String result = "";
        if (birthday != null) {
            result += "Дата рождения: " + birthday + "\n";
        }
        if (height != null) {
            result += "\tРост: " + height + "\n";
        }
        if (weight != null) {
            result += "\tВес: " + weight + "\n";
        }
        return result;
    }

    /**
     * Вычисляет хеш-код объекта
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return birthday.hashCode() + height.hashCode() + weight.hashCode();
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
        Person person = (Person) o;
        return birthday.equals(person.birthday) &&
                height.equals(person.height) &&
                weight.equals(person.weight);
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (height == null) {
            throw new IllegalArgumentException("Рост не может быть null");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Рост должен быть больше 0");
        }
        if (weight != null && weight <= 0) {
            throw new IllegalArgumentException("Вес должен быть больше 0");
        }
    }
}