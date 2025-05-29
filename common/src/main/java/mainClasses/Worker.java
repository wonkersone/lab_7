package mainClasses;

import interfaces.Validatable;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Objects;

/**
 * Класс, представляющий работника
 * Содержит всю информацию о работнике: личные данные, должность, зарплату и даты работы
 */
public class Worker implements Validatable, Serializable {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private long salary; //Значение поля должно быть больше 0
    private LocalDateTime startDate; //Поле не может быть null
    private LocalDateTime endDate; //Поле может быть null
    private Position position; //Поле не может быть null
    private Person person; //Поле может быть null
    private String createdBy; //Владелец объекта

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final DateTimeFormatter zonedDateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm z");

    /**
     * Конструктор без параметров
     */
    public Worker() {}

    /**
     * Конструктор с параметрами
     * @param id уникальный идентификатор
     * @param name имя работника
     * @param coordinates координаты работника
     * @param creationDate дата создания записи
     * @param salary зарплата работника
     * @param startDate дата начала работы
     * @param endDate дата окончания работы
     * @param position должность работника
     * @param person личные данные работника
     */
    public Worker(Integer id, String name, Coordinates coordinates, LocalDate creationDate, long salary,
                 LocalDateTime startDate, LocalDateTime endDate, Position position, Person person, String createdBy) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.salary = salary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.position = position;
        this.person = person;
        this.createdBy = createdBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Получает идентификатор работника
     * @return идентификатор
     */
    public Integer getId() {
        return id;
    }

    /**
     * Получает имя работника
     * @return имя
     */
    public String getName() {
        return name;
    }

    /**
     * Получает координаты работника
     * @return координаты
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Получает дату создания записи
     * @return дата создания
     */
    public LocalDate getCreationDate() {
        return creationDate;
    }

    /**
     * Получает зарплату работника
     * @return зарплата
     */
    public long getSalary() {
        return salary;
    }

    /**
     * Получает дату начала работы
     * @return дата начала работы
     */
    public LocalDateTime getStartDate() {
        return startDate;
    }

    /**
     * Получает дату окончания работы
     * @return дата окончания работы
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    /**
     * Получает должность работника
     * @return должность
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Получает личные данные работника
     * @return личные данные
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Устанавливает идентификатор работника
     * @param id идентификатор
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Устанавливает имя работника
     * @param name имя
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Устанавливает координаты работника
     * @param coordinates координаты
     */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Устанавливает дату создания записи
     * @param creationDate дата создания
     */
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Устанавливает зарплату работника
     * @param salary зарплата
     */
    public void setSalary(long salary) {
        this.salary = salary;
    }

    /**
     * Устанавливает дату начала работы
     * @param startDate дата начала работы
     */
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    /**
     * Устанавливает дату окончания работы
     * @param endDate дата окончания работы
     */
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    /**
     * Устанавливает должность работника
     * @param position должность
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Устанавливает личные данные работника
     * @param person личные данные
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * Преобразует объект в строковое представление
     * @return строковое представление работника со всеми его данными
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Работник:\n");
        result.append("Владелец: ").append(createdBy).append("\n");
        result.append("  ID: ").append(id != null ? id : "не установлен").append("\n");
        result.append("  Имя: ").append(name != null ? name : "не указано").append("\n");
        result.append("  Координаты: ").append(coordinates != null ? coordinates : "не указаны").append("\n");
        result.append("  Дата создания: ").append(creationDate != null ? creationDate.format(dateFormatter) : "не указана").append("\n");
        result.append("  Зарплата: ").append(salary).append("\n");
        result.append("  Дата начала работы: ").append(startDate != null ? startDate.format(dateTimeFormatter) : "не указана").append("\n");
        result.append("  Дата окончания работы: ").append(endDate != null ? endDate.format(dateFormatter) : "не указана").append("\n");
        result.append("  Должность: ").append(position != null ? position.getValue() : "не указана").append("\n");

        if (person != null) {
            result.append("  Информация о человеке:\n");
            result.append("    ").append(person.toString());
        } else {
            result.append("  Нет данных о человеке");
        }

        return result.toString();
    }

    /**
     * Вычисляет хеш-код объекта
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return id.hashCode() + name.hashCode() + coordinates.hashCode() + creationDate.hashCode()
                +   startDate.hashCode() + endDate.hashCode() + position.hashCode() + person.hashCode();
    }

    public static class WorkerComparatorSalary implements Comparator<Worker> {
        @Override
        public int compare(Worker o1, Worker o2) {
            if (o1.getSalary() > o2.getSalary()) {
                return 1;
            } else if (o1.getSalary() == o2.getSalary()) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    public static class WorkerComparatorCreatingDate implements Comparator<Worker> {

        @Override
        public int compare(Worker w1, Worker w2) {
            if (w1.getCreationDate().isAfter(w2.creationDate)) {
                return 1;
            } else if (w1.getCreationDate().isEqual(w2.getCreationDate())) {
                return 0;
            } else {
                return -1;
            }
        }
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
        Worker worker = (Worker) o;
        return salary == worker.salary &&
                id.equals(worker.id) &&
                name.equals(worker.name) &&
                coordinates.equals(worker.coordinates) &&
                creationDate.equals(worker.creationDate) &&
                startDate.equals(worker.startDate) &&
                position.equals(worker.position) &&
                Objects.equals(endDate, worker.endDate) &&
                Objects.equals(person, worker.person);
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (coordinates == null) {
            throw new IllegalArgumentException("Координаты не могут быть null");
        }
        coordinates.validate();
        
        if (creationDate == null) {
            throw new IllegalArgumentException("Дата создания не может быть null");
        }
        if (salary <= 0) {
            throw new IllegalArgumentException("Зарплата должна быть больше 0");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Дата начала работы не может быть null");
        }
        if (position == null) {
            throw new IllegalArgumentException("Должность не может быть null");
        }
        if (person != null) {
            person.validate();
        }
    }
}