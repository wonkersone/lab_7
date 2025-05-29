package commands;

import mainClasses.Worker;
import managers.CollectionManager;
import managers.DatabaseManager;

import java.sql.SQLException;

/**
 * Команда 'remove_lower'
 * Удаляет из коллекции все элементы, меньшие, чем заданный
 */
public class RemoveLowerCommand extends Command {

    /**
     * Создает команду remove_lower
     */
    public RemoveLowerCommand() {
        super("remove_lower", "удалить из коллекции все элементы, меньшие, чем заданный",
                CommandType.WITH_WORKER_DATA, CommandAccess.WITH_ACCESS, false);
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
        StringBuilder result = new StringBuilder();
        if (args.length > 0) {
            result.append("Данная команда не принимает аргументы!");
            return result.toString();
        } else {
            for (Worker workerToRemove : collectionManager.getWorkersCollection()) {
                if (workerToRemove.getSalary() < worker.getSalary() && workerToRemove.getCreatedBy().equals(username)) {
                    try {
                        if (DatabaseManager.deleteWorker(workerToRemove)) {
                            collectionManager.getWorkersCollection().remove(workerToRemove);
                            result.append("Удален работик: ").append(workerToRemove.getName()).append("\n");
                        } else {
                            result.append("Работник ").append(workerToRemove.getName()).append(" не удален из БД потому что потому\n");
                        }
                    } catch (SQLException e) {
                        result.append("Ошибка при удалении элемента из БД: ").append(e.getMessage());
                    }
                }
            }

            result.append("Удалены элементы, меньшие чем заданный и принадлежащие " + username);
        }
        return result.toString();
    }
}