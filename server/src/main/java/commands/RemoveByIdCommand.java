package commands;

import mainClasses.Worker;
import managers.CollectionManager;
import managers.DatabaseManager;

import java.sql.SQLException;

/**
 * Команда 'remove_by_id'
 * Удаляет элемент из коллекции по его id
 */
public class RemoveByIdCommand extends Command {

    /**
     * Создает команду remove_by_id
     */
    public RemoveByIdCommand() {
        super("remove_by_id", "удалить элемент из коллекции по его id",
                CommandType.WITHOUT_WORKER_DATA, CommandAccess.WITH_ACCESS, true);
    }

    /**
     * Исполняет команду
     *
     * @param args              аргументы команды (id элемента для удаления)
     * @param collectionManager менеджер коллекции
     * @param username
     */
    @Override
    public String execute(String[] args, CollectionManager collectionManager, String username) {
        try {
            if (args.length != 1) throw new IllegalArgumentException();
            int id = Integer.parseInt(args[0]);
            Worker worker = collectionManager.findWorkerById(id);
            if (worker == null) {
                return "Объект с id = " + id + " не найден";
            } else {
                if (worker.getCreatedBy().equals(username)) {
                    if (DatabaseManager.deleteWorker(worker)) {
                        collectionManager.removeElement(id);
                        return "Объект успешно удален";
                    } else {
                        return "Ошибка при удалении работника с id " + id;
                    }
                } else {
                    return "У пользователя " + username + " нет прав на удаление объекта с id " + id;
                }
            }
        } catch (IllegalArgumentException e) {
            return "Использование: remove_by_id [id]";
        } catch (SQLException exception) {
            return "Ошибка при удалении работника из БД: " + exception.getMessage();
        }
    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker, String username) {
        return "";
    }
}