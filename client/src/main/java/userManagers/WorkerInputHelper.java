package userManagers;

import mainClasses.Coordinates;
import mainClasses.Person;
import mainClasses.Position;
import mainClasses.Worker;
import shit.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Scanner;

/**
 * Вспомогательный класс для ввода данных работника
 * Предоставляет методы для интерактивного ввода всех полей работника через консоль
 */
public class WorkerInputHelper {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Создает нового работника путем последовательного ввода всех его полей
     * @return новый объект Worker с введенными данными
     */
    public  Worker inputWorker(User user) {
        Worker worker = new Worker();
        worker.setCreatedBy(user.getUserName());

        worker.setName(inputName());
        worker.setCoordinates(inputCoordinates());
        worker.setSalary(inputSalary());
        worker.setStartDate(inputStartDate());
        worker.setEndDate(inputEndDate());
        worker.setPosition(inputPosition());
        worker.setPerson(inputPerson());

        return worker;
    }

    /**
     * Запрашивает ввод имени работника
     * @return введенное имя
     */
    private static String inputName() {
        while (true) {
            System.out.print("Введите имя: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Ошибка: имя не может быть пустым.");
                continue;
            }

            if (name.matches("\\d+")) {
                System.out.println("Ошибка: имя не может состоять из цифр.");
                continue;
            }
            return name;
        }
    }

    /**
     * Создает объект координат путем ввода x и y
     * @return новый объект Coordinates
     */
    private static Coordinates inputCoordinates() {
        return new Coordinates(inputXCoordinate(), inputYCoordinate());
    }

    /**
     * Запрашивает ввод координаты X
     * @return введенное значение координаты X
     */
    private static Double inputXCoordinate() {
        while (true) {
            try {
                System.out.print("Введите координату x: ");
                String x = scanner.nextLine();
                if (!x.isEmpty()) {
                    return Double.parseDouble(x);
                }
                System.out.println("Ошибка! Значения поля Х не может быть null!");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Введено некорректное число!");
            }
        }
    }

    /**
     * Запрашивает ввод координаты Y
     * @return введенное значение координаты Y
     */
    private static long inputYCoordinate() {
        while (true) {
            try {
                System.out.print("Введите координату y: ");
                String y = scanner.nextLine();

                if (!y.isEmpty() && Long.parseLong(y) > -415) {
                    return Long.parseLong(y);
                }
                System.out.println("Значение поля y должно быть больше -415 и не может быть пустым.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Введено некорректное число!");
            }
        }
    }

    /**
     * Запрашивает ввод зарплаты
     * @return введенное значение зарплаты
     */
    private static long inputSalary() {
        while (true) {
            try {
                System.out.print("Введите зарплату: ");
                long salary = Long.parseLong(scanner.nextLine());
                if (salary <= 0) {
                    System.out.println("Ошибка: зарплата должна быть больше 0.");
                    continue;
                }
                return salary;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введено некорректное число.");
            }
        }
    }

    /**
     * Запрашивает ввод даты начала работы
     * @return введенная дата начала работы
     */
    private static LocalDateTime inputStartDate() {
        while (true) {
            try {
                System.out.print("Введите дату начала работы (ДД-ММ-ГГГГ): ");
                String dateInput = scanner.nextLine();
                LocalDate date = LocalDate.parse(dateInput, dateFormatter);
                return date.atStartOfDay(); // Преобразуем LocalDate в LocalDateTime
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: некорректная дата. Убедитесь, что дата существует (например, 30 февраля не существует).");
            }
        }
    }

    /**
     * Запрашивает ввод даты окончания работы
     * @return введенная дата окончания работы или null, если дата не указана
     */
    private static LocalDateTime inputEndDate() {
        while (true) {
            try {
                System.out.print("Введите дату окончания работы (ДД-ММ-ГГГГ): ");
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    return null;
                }
                LocalDate date = LocalDate.parse(input, dateFormatter);
                return date.atStartOfDay().atZone(ZoneId.systemDefault()).toLocalDateTime(); // Преобразуем в ZonedDateTime
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: некорректная дата. Убедитесь, что дата существует (например, 30 февраля не существует).");
            }
        }
    }

    /**
     * Запрашивает ввод должности
     * @return введенная должность
     */
    private static Position inputPosition() {
        while (true) {
            try {
                System.out.print("Введите должность (пекарь, уборщик, менеджер по уборке): ");
                String input = scanner.nextLine().trim().toLowerCase();

                // Поиск соответствующего значения перечисления по value
                for (Position position : Position.values()) {
                    if (position.getValue().equalsIgnoreCase(input)) {
                        return position;
                    }
                }

                // Если введенное значение не найдено
                System.out.println("Ошибка: некорректная должность. Доступные значения: пекарь, уборщик, менеджер по уборке.");
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: некорректная должность. Доступные значения: пекарь, уборщик, менеджер по уборке.");
            }
        }
    }

    /**
     * Запрашивает ввод данных о человеке (рост, вес, дата рождения)
     * @return новый объект Person с введенными данными
     */
    private static Person inputPerson() {
        while (true) {
            try {
                System.out.print("Введите рост: ");
                Float height = Float.parseFloat(scanner.nextLine());
                if (height <= 0) {
                    System.out.println("Ошибка: рост должен быть больше 0.");
                    continue;
                }

                System.out.print("Введите вес: ");
                String weightInput = scanner.nextLine();
                Float weight = weightInput.isEmpty() ? null : Float.parseFloat(weightInput);
                if (weight != null && weight <= 0) {
                    System.out.println("Ошибка: вес должен быть больше 0.");
                    continue;
                }

                System.out.print("Введите дату рождения (ДД-ММ-ГГГГ или оставьте пустым): ");
                String birthdayInput = scanner.nextLine();
                Date birthday = null;
                if (!birthdayInput.isEmpty()) {
                    try {
                        LocalDate date = LocalDate.parse(birthdayInput, dateFormatter);
                        birthday = java.sql.Date.valueOf(date); // Преобразуем LocalDate в java.util.Date
                    } catch (DateTimeParseException e) {
                        System.out.println("Ошибка: некорректная дата. Убедитесь, что дата существует (например, 30 февраля не существует).");
                        continue;
                    }
                }

                return new Person(birthday, height, weight);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введено некорректное число.");
            }
        }
    }
}