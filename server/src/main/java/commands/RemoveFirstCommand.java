package commands;

import mainClasses.Worker;
import managers.CollectionManager;

/**
 * Команда 'remove_first'
 * Удаляет первый элемент из коллекции
 */
public class RemoveFirstCommand extends Command {

    /**
     * Создает команду remove_first
     */
    public RemoveFirstCommand() {
        super("remove_first", "удалить первый элемент из коллекции",
                CommandType.WITHOUT_WORKER_DATA, CommandAccess.WITH_ACCESS, false);
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
        if (args.length > 0) {
            return "Данная команда не принимает аргументов!";
        } else if (collectionManager.getWorkersCollection().isEmpty()) {
            return "Коллекция пустая! Удалять нечего...";
        } else {
            return collectionManager.removeFirstElement(username);
        }
    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker, String username) {
        return "";
    }
}