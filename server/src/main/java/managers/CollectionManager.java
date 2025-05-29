package managers;

import mainClasses.Worker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Менеджер коллекции работников
 * Управляет коллекцией объектов Worker, обеспечивая операции добавления, удаления и модификации элементов
 */
public class CollectionManager {
    private ArrayDeque<Worker> workersCollection;
    private final LocalDate creationDate;
    Connection connection;


    /**
     * Создает новый менеджер коллекции
     * Инициализирует пустую коллекцию и устанавливает дату создания
     */
    public CollectionManager() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("C:\\Users\\MSI\\IdeaProjects\\Lab_7\\server\\src\\main\\resources\\props.txt"));
            String sshUser = lines.get(0);
            String sshPassword = lines.get(1);
            String sshHost = lines.get(2);
            int sshPort = Integer.parseInt(lines.get(3));
            int localPort = Integer.parseInt(lines.get(4));
            String dbHost = lines.get(5);
            int dbPort = Integer.parseInt(lines.get(6));
            String dbName = lines.get(7);
            String dbUser = sshUser;
            String dbPassword = lines.get(8);

            DatabaseManager.initialize(sshUser, sshPassword, sshHost, sshPort,
                    localPort, dbHost, dbPort,
                    dbName, dbUser, dbPassword);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
//        // Параметры SSH (для Хелиоса)
//        String sshUser = "s466497";          // Ваш логин на Хелиосе
//        String sshPassword = "hbMV!1213";   // Пароль SSH
//        String sshHost = "se.ifmo.ru";       // Сервер Хелиоса
//        int sshPort = 2222;                  // Порт SSH
//
//        // Параметры туннеля
//        int localPort = 8888;                // Локальный порт для подключения
//
//        // Параметры БД за SSH
//        String dbHost = "pg";                // Хост БД в сети Хелиоса
//        int dbPort = 5432;                   // Порт PostgreSQL
//
//        // Параметры подключения к БД
//        String dbName = "studs";             // Имя вашей БД
//        String dbUser = sshUser;             // Обычно такой же как SSH-логин
//        String dbPassword = "dGk1O9wpCgdr1Klu";     // Пароль к БД


        // Получаем соединение и работаем с БД
        this.connection = DatabaseManager.getConnection();

        this.workersCollection = new ArrayDeque<Worker>();
        creationDate = LocalDate.now();
    }


    //TODO метод должен загружать коллекцию из БД
    public String loadCollectionFromFile() {
        this.workersCollection = DatabaseManager.loadAllWorkers();
        return this.workersCollection.stream().map(Worker::getName).collect(Collectors.joining(","));
    }

    //TODO метод должен сохранять коллекцию в БД
    public void saveCollectionToFile() {
    }

    /**
     * Возвращает дату создания коллекции
     * @return дата создания
     */
    public LocalDate getCreationDate() {
        return creationDate;
    }

    /**
     * Возвращает коллекцию работников
     * @return коллекция работников
     */
    public ArrayDeque<Worker> getWorkersCollection() {
        return workersCollection;
    }

    /**
     * Устанавливает новую коллекцию работников
     * @param workersCollection новая коллекция
     */
    public void setWorkersCollection(ArrayDeque<Worker> workersCollection) {
        this.workersCollection = workersCollection;
    }

    /**
     * Возвращает информацию о коллекции
     * @return строка с информацией о типе, дате создания и размере коллекции
     */
    public String getCollectionInfo() {
        return ("Type - " + workersCollection.getClass().getName().substring(10,20) +
                "\nCreation date - " + getCreationDate() +
                "\nAmount of elements - " + workersCollection.size());
    }

    /**
     * Выводит все элементы коллекции
     */
    public String showCollectionElements() {
        String res;
        if (workersCollection.isEmpty()) {
            res = "Коллекция пуста";
        } else {
            res = workersCollection.stream()
                    .sorted(Comparator.comparing(Worker::getName))
                    .map(Worker::toString).collect(Collectors.joining("\n"));
        }
        return res;
    }

    /**
     * Добавляет нового работника в коллекцию
     * @param worker новый работник
     */
    public void addElement(Worker worker) {
        if (worker == null) {
            throw new IllegalArgumentException("Работник не может быть null");
        }
        worker.setId(generateId());
        worker.setCreationDate(LocalDate.now());
        if (worker.getName() == null || worker.getName().isEmpty()) {
            throw new IllegalArgumentException("Имя работника не может быть пустым");
        }
        if (worker.getCoordinates() == null) {
            throw new IllegalArgumentException("Координаты работника не могут быть null");
        }
        if (worker.getStartDate() == null) {
            throw new IllegalArgumentException("Дата начала работы не может быть null");
        }
        if (worker.getPosition() == null) {
            throw new IllegalArgumentException("Должность работника не может быть null");
        }
        if (worker.getSalary() <= 0) {
            throw new IllegalArgumentException("Зарплата должна быть больше 0");
        }

        try {
            if (DatabaseManager.addWorker(worker)) {
                workersCollection.add(worker);
            }
        } catch (SQLException e) {
            System.err.println("SQLException при добавлении работника: " + e.getMessage());
        }
    }

    /**
     * Обновляет данные работника по его id
     * @param id идентификатор работника
     * @param new_worker новые данные работника
     */
    public String updateElement(int id, Worker new_worker, String username) {
        Worker worker = findWorkerById(id);
        if (worker != null) {
            if (worker.getCreatedBy().equals(username)) {
                try {
                    if (DatabaseManager.updateWorker(id, new_worker)) {
                        workersCollection.stream().filter(w -> w.getId() == id).findFirst().
                                ifPresent(w -> {
                                    w.setId(new_worker.getId());
                                    w.setName(new_worker.getName());
                                    w.setCoordinates(new_worker.getCoordinates());
                                    w.setSalary(new_worker.getSalary());
                                    w.setStartDate(new_worker.getStartDate());
                                    w.setEndDate(new_worker.getEndDate());
                                    w.setPosition(new_worker.getPosition());
                                    w.setPerson(new_worker.getPerson());
                                });
                        return "Данные о работнике обновлены успешно!";
                    } else {
                        return "Ошибка обновления данных о работнике!";
                    }
                } catch (SQLException e) {
                    return "Ошибка при обновлении данных работника! " + e.getMessage();
                }
            } else {
                return "У пользователя " + username + " нет прав на обновление данных работника с id " + id;
            }
        } else {
            return "Работника с id " + id + "нет в коллекции!";
        }
    }

    /**
     * Удаляет работника по его id
     * @param id идентификатор работника
     */
    public void removeElement(int id) {
        workersCollection.removeIf(worker -> worker.getId() == id);
    }

    /**
     * Очищает коллекцию
     */
    public String clearCollection(String username) {
        StringBuilder res = new StringBuilder();
        res.append("Элементы, принадлежащие ").append(username).append(":\n");
//        workersCollection.removeIf(worker -> worker.getCreatedBy().equals(username));
        for (Worker worker : workersCollection) {
            if (worker.getCreatedBy().equals(username)) {
                try {
                    if (DatabaseManager.deleteWorker(worker)) {
                        res.append(worker.getName()).append(" - удален;");
                        workersCollection.remove(worker);
                    } else {
                        res.append(worker.getName()).append(" - не удален");
                    }
                } catch (SQLException e) {
                    res.append("Ошибка во время удаления из БД: ").append(e.getMessage());
                }
            }
        }
        return res.toString();
    }

    /**
     * Удаляет первый элемент коллекции
     */
    public String removeFirstElement(String username) {
        Worker worker = workersCollection.getFirst();
        if (worker.getCreatedBy().equals(username)) {
            try {
                if (DatabaseManager.deleteWorker(worker)) {
                    workersCollection.pollFirst();
                    return "Первый элемент удален.";
                } else {
                    return "Работник не удален из коллекции, возможно, потому что его там и не было, хз";
                }
            } catch (SQLException e) {
                return "Ошибка во время удаления работника из БД: " + e.getMessage();
            }

        } else {
            return "У пользователя " + username + " нет прав на удаление первого элемента";
        }
    }

    /**
     * Добавляет элемент, если его зарплата меньше минимальной в коллекции
     * @param worker работник для добавления
     * @return true если элемент был добавлен, false если нет
     */
    public boolean addElementIfMin(Worker worker) {
        Worker minWorker = workersCollection.stream()
                .min(Comparator.comparing(Worker::getSalary)).orElse(null);
        if (minWorker == null || worker.getSalary() < minWorker.getSalary()) {
            workersCollection.add(worker);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Удаляет всех работников с зарплатой меньше заданной
     * @param worker работник для сравнения
     * @return true если были удалены элементы, false если нет
     */
    public boolean removeLowerElement(Worker worker) {
        return workersCollection.removeIf(w -> w.getSalary() < worker.getSalary());
    }

    /**
     * Выводит значения поля salary в порядке возрастания
     */
    public void printFieldAscendingSalary() {
        if (workersCollection.isEmpty()) {
            System.out.println("Коллекция пуста");
            return;
        }
        workersCollection.stream()
                .sorted(Comparator.comparing(Worker::getSalary))
                .forEach(w -> System.out.println(w.getSalary()));
    }

    /**
     * Возвращает работника с минимальной датой создания
     * @return строковое представление работника или сообщение о пустой коллекции
     */
    public String minByCreationDate() {
        if (workersCollection.isEmpty()) {
            return "Коллекция пуста";
        }
        Worker minWorker = workersCollection.stream()
                .min(Comparator.comparing(Worker::getCreationDate))
                .orElse(null);
        return minWorker != null ? minWorker.toString() : "Коллекция пуста";
    }

    /**
     * Возвращает сумму зарплат всех работников
     * @return сумма зарплат
     */
    public long sumOfSalary() {
        return workersCollection.stream()
                .mapToLong(Worker::getSalary)
                .sum();
    }

    /**
     * Генерирует уникальный идентификатор для нового работника
     * @return новый уникальный id, на единицу больше максимального в коллекции
     */
    public int generateId() {
        if (workersCollection.isEmpty()) {
            return 1;
        }
        return workersCollection.stream()
                .mapToInt(Worker::getId)
                .max()
                .orElse(0) + 1;
    }

    public Worker findWorkerById(int id) {
        for (Worker worker : workersCollection) {
            if (worker.getId() == id) {
                return worker;
            }
        }
        return null;
    }
}