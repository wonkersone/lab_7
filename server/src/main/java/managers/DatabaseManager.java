package managers;

import com.jcraft.jsch.Session;
import mainClasses.Coordinates;
import mainClasses.Person;
import mainClasses.Position;
import mainClasses.Worker;
import network.SSHConnection;
import network.Security;
import shit.User;


import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.HashMap;

public class DatabaseManager {
    private static Connection connection;
    private static Session sshSession;
    private static SSHConnection sshTunnel;


    public static void initialize(String sshUser, String sshPassword,
                                  String sshHost, int sshPort,
                                  int localPort, String dbHost, int dbPort,
                                  String dbName, String dbUser, String dbPassword) throws SQLException {
        // 1. Создаём и запускаем SSH-туннель
        sshTunnel = new SSHConnection(sshUser, sshPassword, sshHost, sshPort,
                localPort, dbHost, dbPort);

        Thread tunnelThread = new Thread(sshTunnel);
        tunnelThread.setDaemon(true);
        tunnelThread.start();

        try {
            // 2. Ждём установки соединения (макс 10 сек)
            if (!sshTunnel.waitUntilConnected(10000)) {
                throw new SQLException("Не удалось установить SSH-соединение");
            }

            // 3. Подключаемся к БД через туннель
            String jdbcUrl = String.format("jdbc:postgresql://localhost:%d/%s",
                    localPort, dbName);

            connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
            System.out.println("Успешное подключение к БД через SSH-туннель");

        } catch (InterruptedException e) {
            throw new SQLException("Ожидание подключения прервано", e);
        }
    }




    public static Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("Сначала инициализируйте подключение");
        }
        return connection;
    }

    public static boolean addWorker(Worker worker) throws SQLException {
        PreparedStatement coordsStmt = null;
        PreparedStatement personStmt = null;
        PreparedStatement workerStmt = null;
        ResultSet generatedKeys = null;

        try {
            connection.setAutoCommit(false);
            String coordsSql = "INSERT INTO coordinates (x, y) VALUES (?, ?)";
            coordsStmt = connection.prepareStatement(coordsSql, Statement.RETURN_GENERATED_KEYS);
            coordsStmt.setDouble(1, worker.getCoordinates().getX());
            coordsStmt.setLong(2, worker.getCoordinates().getY());
            coordsStmt.executeUpdate();

            int coordsId;
            generatedKeys = coordsStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                coordsId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Не удалось получить ID!");
            }

            String personSql = "INSERT INTO persons (birthday, height, weight) VALUES (?, ?, ?)";
            personStmt = connection.prepareStatement(personSql, Statement.RETURN_GENERATED_KEYS);
            if (worker.getPerson().getBirthday() != null) {
                personStmt.setDate(1, Date.valueOf(String.valueOf(worker.getPerson().getBirthday())));
            } else {
                personStmt.setNull(1, Types.DATE);
            }

            personStmt.setFloat(2, worker.getPerson().getHeight());
            personStmt.setFloat(3, worker.getPerson().getWeight());
            personStmt.executeUpdate();

            int personId;
            generatedKeys = personStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                personId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Не удалось получить ID персональных данных работника!");
            }

            String workerSql = "INSERT INTO workers (name, coordinates_id, salary, " +
                    "start_date, end_date, position, person_id, created_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            workerStmt = connection.prepareStatement(workerSql);
            workerStmt.setString(1, worker.getName());
            workerStmt.setInt(2, coordsId);
            workerStmt.setLong(3, worker.getSalary());
            if (worker.getStartDate() == null) {
                throw new IllegalArgumentException("Дата начала работы не может быть null");
            }
            LocalDate startLocalDate = worker.getStartDate().toLocalDate();
            Date startDate = Date.valueOf(startLocalDate);
            workerStmt.setDate(4, startDate);
            if (worker.getEndDate() != null) {
                workerStmt.setTimestamp(5, Timestamp.valueOf(worker.getEndDate()));
            } else {
                workerStmt.setNull(5, Types.TIMESTAMP);
            }
            workerStmt.setString(6, String.valueOf(worker.getPosition()));
            workerStmt.setInt(7, personId);
            workerStmt.setString(8, worker.getCreatedBy());

            int affectedRows = workerStmt.executeUpdate();
            connection.commit();
            return affectedRows > 0;
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true); // Важно для пулов соединений!
            }
            if (generatedKeys != null) generatedKeys.close();
            if (workerStmt != null) workerStmt.close();
            if (personStmt != null) personStmt.close();
            if (coordsStmt != null) coordsStmt.close();
        }
    }

    public static boolean updateWorker(int workerId, Worker updatedWorker) throws SQLException {
        PreparedStatement selectStmt = null;
        PreparedStatement coordsStmt = null;
        PreparedStatement personStmt = null;
        PreparedStatement workerStmt = null;
        ResultSet rs = null;

        try {
            connection.setAutoCommit(false);

            String selectSql = "SELECT coordinates_id, person_id FROM workers WHERE id = ?";
            selectStmt = connection.prepareStatement(selectSql);
            selectStmt.setInt(1, workerId);
            rs = selectStmt.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Работник с ID " + workerId + " не найден");
            }

            int coordinatesId = rs.getInt("coordinates_id");
            int personId = rs.getInt("person_id");

            // 2. Обновляем координаты
            String coordsSql = "UPDATE coordinates SET x = ?, y = ? WHERE id = ?";
            coordsStmt = connection.prepareStatement(coordsSql);
            coordsStmt.setDouble(1, updatedWorker.getCoordinates().getX());
            coordsStmt.setLong(2, updatedWorker.getCoordinates().getY());
            coordsStmt.setInt(3, coordinatesId);
            coordsStmt.executeUpdate();

            // 3. Обновляем персональные данные
            String personSql = "UPDATE persons SET birthday = ?, height = ?, weight = ? WHERE id = ?";
            personStmt = connection.prepareStatement(personSql);
            if (updatedWorker.getPerson().getBirthday() != null) {
                personStmt.setDate(1, Date.valueOf(String.valueOf(updatedWorker.getPerson().getBirthday())));
            } else {
                personStmt.setNull(1, Types.DATE);
            }
            personStmt.setFloat(2, updatedWorker.getPerson().getHeight());
            personStmt.setFloat(3, updatedWorker.getPerson().getWeight());
            personStmt.setInt(4, personId);
            personStmt.executeUpdate();

            // 4. Обновляем данные работника
            String workerSql = "UPDATE workers SET name = ?, salary = ?, " +
                    "start_date = ?, end_date = ?, position = ? " +
                    "WHERE id = ?";
            workerStmt = connection.prepareStatement(workerSql);
            workerStmt.setString(1, updatedWorker.getName());
            workerStmt.setLong(2, updatedWorker.getSalary());
            workerStmt.setDate(3, Date.valueOf(updatedWorker.getStartDate().toLocalDate()));

            if (updatedWorker.getEndDate() != null) {
                workerStmt.setTimestamp(4, Timestamp.valueOf(updatedWorker.getEndDate()));
            } else {
                workerStmt.setNull(4, Types.TIMESTAMP);
            }

            workerStmt.setString(5, updatedWorker.getPosition().name());
            workerStmt.setInt(6, workerId);

            int affectedRows = workerStmt.executeUpdate();
            connection.commit();
            return affectedRows > 0;
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
            if (rs != null) rs.close();
            if (selectStmt != null) selectStmt.close();
            if (workerStmt != null) workerStmt.close();
            if (personStmt != null) personStmt.close();
            if (coordsStmt != null) coordsStmt.close();
        }
    }

    public static boolean addWorkerIfMin(Worker worker) throws SQLException {
        String minSalarySql = "SELECT MIN(salary) AS min_salary FROM workers";
        try (PreparedStatement stmt = connection.prepareStatement(minSalarySql);
        ResultSet rs = stmt.executeQuery()) {
            double minSalary = Double.MAX_VALUE;
            if (rs.next()) {
                minSalary = rs.getDouble("min_salary");
                if (rs.wasNull()) {
                    minSalary = Double.MAX_VALUE;
                }
            }

            if (worker.getSalary() >= minSalary) {
                return false;
            }

            return addWorker(worker);
        } catch (SQLException e) {
            throw new SQLException("Ошибка при проверке минимальной зарплаты", e);
        }
    }

    public static boolean deleteWorker(Worker worker) throws SQLException {
        if (worker == null) {
            throw new IllegalArgumentException("Работник не может быть null");
        }

        Connection connection = null;
        PreparedStatement deleteWorkerStmt = null;
        PreparedStatement deletePersonStmt = null;
        PreparedStatement deleteCoordsStmt = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            // 1. Получаем связанные ID
            String selectSql = "SELECT coordinates_id, person_id FROM workers WHERE id = ?";
            int coordinatesId = -1;
            int personId = -1;

            try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
                selectStmt.setInt(1, worker.getId());
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        coordinatesId = rs.getInt("coordinates_id");
                        personId = rs.getInt("person_id");
                    } else {
                        return false; // Работник не найден
                    }
                }
            }

            // 2. Удаляем работника
            String deleteWorkerSql = "DELETE FROM workers WHERE id = ?";
            deleteWorkerStmt = connection.prepareStatement(deleteWorkerSql);
            deleteWorkerStmt.setInt(1, worker.getId());
            int affectedRows = deleteWorkerStmt.executeUpdate();

            if (affectedRows == 0) {
                connection.rollback();
                return false;
            }

            // 3. Удаляем персональные данные
            String deletePersonSql = "DELETE FROM persons WHERE id = ?";
            deletePersonStmt = connection.prepareStatement(deletePersonSql);
            deletePersonStmt.setInt(1, personId);
            deletePersonStmt.executeUpdate();

            // 4. Удаляем координаты
            String deleteCoordsSql = "DELETE FROM coordinates WHERE id = ?";
            deleteCoordsStmt = connection.prepareStatement(deleteCoordsSql);
            deleteCoordsStmt.setInt(1, coordinatesId);
            deleteCoordsStmt.executeUpdate();

            connection.commit();
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
            if (deleteWorkerStmt != null) deleteWorkerStmt.close();
            if (deletePersonStmt != null) deletePersonStmt.close();
            if (deleteCoordsStmt != null) deleteCoordsStmt.close();
        }
    }

    public static boolean saveUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Генерируем соль для пароля
            String salt = Security.generateSalt();

            // Хешируем пароль с солью
            String hashedPassword = Security.hashPassword(user.getPassword(), salt);

            // Устанавливаем параметры запроса
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, salt);

            // Выполняем запрос
            int affectedRows = pstmt.executeUpdate();

            // Возвращаем true если была добавлена 1 строка
            return affectedRows == 1;
        } catch (SQLException e) {
            // Обработка возможных ошибок
            if (e.getSQLState().equals("23505")) { // Код ошибки дублирования ключа
                System.out.println("Ошибка: пользователь с таким именем уже существует");
            } else {
                System.out.println("Ошибка при сохранении пользователя: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }
    }

    public static HashMap<String, String> loadAllUsers() {
        HashMap<String, String> usersMap = new HashMap<>();

        String sql = "SELECT username, password_hash FROM users";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String username = rs.getString("username");
                String passwordHash = rs.getString("password_hash");
                usersMap.put(username, passwordHash);
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при загрузке пользователей: " + e.getMessage());
            e.printStackTrace();
        }

        return usersMap;
    }

    public static boolean verifyUser(String username, String inputPassword) {
        String sql = "SELECT password_hash, salt FROM users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String salt = rs.getString("salt");
                String inputHash = Security.hashPassword(inputPassword, salt);
                return storedHash.equals(inputHash);
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayDeque<Worker> loadAllWorkers() {
        ArrayDeque<Worker> workers = new ArrayDeque<>();

        try (Statement stmt = connection.createStatement()) {
            String sql = "SELECT w.*, p.*, c.* FROM workers w " +
                "JOIN persons p ON w.person_id = p.id " +
                "JOIN coordinates c ON w.coordinates_id = c.id";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Worker worker = new Worker();

                worker.setId(rs.getInt("id"));
                worker.setName(rs.getString("name"));
                worker.setCreationDate(rs.getDate("creation_date").toLocalDate());
                worker.setSalary(rs.getLong("salary"));
                worker.setStartDate(rs.getDate("start_date").toLocalDate().atStartOfDay());


                if (rs.getTimestamp("end_date") != null) {
                    worker.setEndDate(rs.getDate("end_date").toLocalDate().atStartOfDay());
                }
                worker.setPosition(Position.valueOf(rs.getString("position")));

                worker.setCreatedBy(rs.getString("created_by"));

                Person person = new Person();
                person.setBirthday(rs.getDate("birthday"));
                person.setHeight(rs.getFloat("height"));
                person.setWeight(rs.getFloat("weight"));
                worker.setPerson(person);

                Coordinates coordinates = new Coordinates();
                coordinates.setX(rs.getDouble("x"));
                coordinates.setY(rs.getLong("y"));
                worker.setCoordinates(coordinates);

                workers.add(worker);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при загрузке работников: " + e.getMessage());
            e.printStackTrace();
        }
        return workers;
    }
}
