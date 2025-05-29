package commands;

import mainClasses.Worker;
import managers.CollectionManager;
import managers.DatabaseManager;

import java.sql.SQLException;

/**
 * Команда 'add_if_min'
 * Добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
 */
public class AddIfMinCommand extends Command {

    /**
     * Создает команду add_if_min
     */
    public AddIfMinCommand() {
        super("add_if_min", "добавить новый элемент в коллекцию, если его значение меньше, " +
                "чем у наименьшего элемента этой коллекции", CommandType.WITH_WORKER_DATA, CommandAccess.NO_ACCESS, false);
    }

    /**
     * Исполняет команду
     *
     * @param args              аргументы команды (не используются)
     * @param collectionManager менеджер коллекции
     * @param username
     */
    @Override
    public String execute(String[] args, CollectionManager collectionManager, String username) {
        return "";
    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker, String username) {
        try {
            if (DatabaseManager.addWorkerIfMin(worker)) {
                if (collectionManager.addElementIfMin(worker)) {
                    return "Работник добавлен в коллекцию";
                }
            }else {
                return "Работник не добавлен, так как его значение не минимально";
            }

        } catch (SQLException e) {
            return "Работник не добавлен, так как его значение не минимально";
        }
        return "";
    }

}