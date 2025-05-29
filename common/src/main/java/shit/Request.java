package shit;

import mainClasses.Worker;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Класс, представляющий запрос от клиента к серверу
 * Содержит информацию о типе запроса, команде и её параметрах
 */
public class Request implements Serializable {


    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    /**
     * Перечисление типов запросов
     */
    public enum RequestType {
        SCRIPT_TRANSFER, INITIAL_COMMAND, WORKER_DATA, REGISTER_NEW_USER, AUTHORIZE_USER;
    }

    private static final long serialVersionUID = 1L;
    private String userName;
    private String password;
    private RequestType type; // Тип запроса
    private String commandName; // Имя команды
    private String[] args; // Аргументы команды
    private String scriptContent; // Содержимое скрипта
    private Worker worker; // Данные работника

    /**
     * Преобразует объект в строковое представление
     * @return строковое представление запроса
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Запрос: \n");
        if (userName != null) {
            result.append("Отправитель: ").append(userName).append("\n");
        }
        if (password != null) {
            result.append("Пароль: ").append(password).append("\n");
        }
        result.append("Тип запроса: ").append(type).append("\n");
        if (commandName != null) {
            result.append("Команда: ").append(commandName).append("\n");
        }
        if (args != null) {
            result.append("Аргументы: ").append(Arrays.toString(args)).append("\n");
        }
        if (scriptContent != null) {
            result.append("Содержание скрипта: ").append(scriptContent).append("\n");
        }
        if (worker != null) {
            result.append("Передаваемый работник: ").append(worker.toString()).append("\n");
        }
        return result.toString();
    }


    public Request(String userName, String password, RequestType type) {
        this.userName = userName;
        this.password = password;
        this.type = type;
    }

    /**
     * Конструктор для создания запроса с начальной командой
     * @param commandName имя команды
     * @param args аргументы команды
     */
    public Request(String commandName, String[] args, String userName, String password) {
        this.type = RequestType.INITIAL_COMMAND;
        this.commandName = commandName;
        this.args = args;
        this.userName = userName;
        this.password = password;
    }

    /**
     * Конструктор для создания запроса с данными работника
     * @param worker объект работника
     */
    public Request(Worker worker) {
        this.type = RequestType.WORKER_DATA;
        this.worker = worker;
    }

    /**
     * Конструктор для создания запроса со скриптом
     * @param commandName имя команды
     * @param args аргументы команды
     * @param scriptContent содержимое скрипта
     * @param type тип запроса
     */
    public Request(String commandName, String[] args, String scriptContent, RequestType type) {
        this.commandName = commandName;
        this.args = args;
        this.scriptContent = scriptContent;
        this.type = type;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public void setType(RequestType type) {
        this.type = type;
    }


    /**
     * Получает содержимое скрипта
     * @return содержимое скрипта
     */
    public String getScriptContent() { return scriptContent; }

    /**
     * Получает тип запроса
     * @return тип запроса
     */
    public RequestType getType() { return type; }

    /**
     * Получает имя команды
     * @return имя команды
     */
    public String getCommandName() { return commandName; }

    /**
     * Получает аргументы команды
     * @return массив аргументов
     */
    public String[] getArgs() { return args; }

    /**
     * Получает данные работника
     * @return объект работника
     */
    public Worker getWorker() { return worker; }


}
