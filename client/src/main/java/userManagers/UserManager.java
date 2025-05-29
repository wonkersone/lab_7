package userManagers;

import exceptions.ScriptRecursionException;
import mainClasses.Worker;
import network.TCPClient;
import shit.Request;
import shit.Response;
import shit.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Класс для обработки пользовательского ввода и взаимодействия с сервером
 * Поддерживает интерактивный режим работы и выполнение скриптов
 */
public class UserManager {
    /** Клиент для взаимодействия с сервером */
    private final TCPClient client;
    /** Сканер для чтения пользовательского ввода */
    private final Scanner scanner;
    /** Помощник для ввода данных работника */
    private final WorkerInputHelper helper;
    /** Форматтер для дат */
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    /** Множество выполненных скриптов для предотвращения рекурсии */
    private final Set<String> executedScripts = new HashSet<>();
    private final User currentUser = new User();
    public UserAuthorization userAuthorization = new UserAuthorization();

    /**
     * Создает новый экземпляр UserInputScanner
     * @param client TCP клиент для взаимодействия с сервером
     */
    public UserManager(TCPClient client) throws IOException {
        this.client = client;
        this.scanner = new Scanner(System.in);
        this.helper = new WorkerInputHelper();
//        this.currentUser = userAuthorization.authentication(scanner, client);
    }

    public User getCurrentUser() { return currentUser; }


    /**
     * Запускает интерактивный режим работы с пользователем
     * Обрабатывает команды пользователя, отправляет запросы на сервер
     * и выводит результаты выполнения команд
     */
    public void startInteractiveMode(User startUser) throws IOException {

        startUser = userAuthorization.authentication(scanner, client);

        Worker worker = null;

        System.out.println("Клиент запущен в интерактивном режиме!");
        Scanner scanner = new Scanner(System.in).useDelimiter("\n"); // Исправляем работу с переводом строк


        while (true) {
            try {
                System.out.print("> ");
                if (!scanner.hasNext()) {  // Проверяем доступность ввода
                    System.out.println("Нет данных для чтения");
                    break;
                }

                String input = scanner.next().trim();
                if (input.isEmpty()) continue;

                if ("exit".equalsIgnoreCase(input)) {
                    System.out.println("Завершение работы клиента");
                    break;
                } else if ("save".equalsIgnoreCase(input)) {
                    System.out.println("Ошибка: Введена некорректная команда!");
                    continue;
                } else if ("add".equalsIgnoreCase(input) || "remove_lower".equalsIgnoreCase(input) || "add_if_min".equalsIgnoreCase(input) || input.matches("(?i)^update\\s+\\d+$")) {
                    worker = helper.inputWorker(startUser);
                }

                // Создаем запрос
                Request request = createRequest(input, startUser);
                if (worker != null) {
                    request.setWorker(worker);
                    request.setType(Request.RequestType.WORKER_DATA);
                    worker = null;
                    System.out.println("Создан запрос: \n" + request.toString());
                }

                // Отправляем запрос и получаем ответ
                Response response = client.sendRequest(request);
                executedScripts.clear();
                if (response.getType() == Response.ResponseType.ERROR) {
                    System.out.println("Ошибка: " + response.getMessage());
                } else if (response.getType() == Response.ResponseType.ONE_MORE_SCRIPT) {
                    handleNestedScript(response, startUser);
                }
                else {
                    System.out.println("\n" + response.getMessage());
                }

            } catch (ScriptRecursionException exception) {
                System.out.println(exception.getMessage());
            } catch (Exception e) {
                System.out.println("Ошибка подключения к серверу: " + e.getMessage());
                // Восстанавливаем соединение при необходимости
                try {
                    System.out.println("Пытаемся восстановить подключение...");
                    client.disconnect();
                    client.connect("localhost", 8000);
                } catch (IOException ex) {
                    System.out.println("Не удалось восстановить соединение: " + ex.getMessage());
                    break;
                }
            }
        }
        scanner.close();
    }



    /**
     * Создает запрос на основе пользовательского ввода
     * @param input строка с командой и аргументами
     * @return объект запроса
     * @throws IOException если произошла ошибка при чтении файла скрипта
     * @throws ScriptRecursionException если обнаружена рекурсия в скриптах
     */
    private Request createRequest(String input, User currentUser) throws IOException, ScriptRecursionException {
        String[] parts = input.split(" ", 2);
        String commandName = parts[0];
        String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

        if (commandName.equalsIgnoreCase("execute_script")) {
            String scriptPath = args[0];
            checkRecursion(scriptPath);
            String scriptContent = readScriptContent(scriptPath);
            return new Request(
                    commandName,
                    args,
                    scriptContent,
                    Request.RequestType.SCRIPT_TRANSFER
            );
        }
        return new Request(commandName, args, currentUser.getUserName(), currentUser.getPassword());
    }

    /**
     * Проверяет наличие рекурсии в скриптах
     * @param scriptPath путь к скрипту
     * @throws ScriptRecursionException если скрипт уже выполняется
     */
    private void checkRecursion(String scriptPath) throws ScriptRecursionException {
        if (executedScripts.contains(scriptPath)) {
            throw new ScriptRecursionException("Рекурсия! Скрипт " + scriptPath + " уже выполняется");
        }
        executedScripts.add(scriptPath);
    }

    /**
     * Читает содержимое скрипта из файла
     * @param scriptPath путь к файлу скрипта
     * @return содержимое скрипта
     * @throws IOException если произошла ошибка при чтении файла
     */
    private String readScriptContent(String scriptPath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(scriptPath)));
    }

    /**
     * Обрабатывает вложенный скрипт, полученный от сервера
     * @param response ответ сервера с информацией о вложенном скрипте
     */
    private void handleNestedScript(Response response, User startUser) throws IOException {
        String scriptPath = response.getMessage().split(": ")[1];
        try {
            Request scriptRequest = createRequest("execute_script " + scriptPath, startUser);
            Response scriptResponse = client.sendRequest(scriptRequest);
            System.out.println(scriptResponse.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка выполнения вложенного скрипта: " + e.getMessage());
        }
    }
}