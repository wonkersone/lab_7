package commands;

import mainClasses.Worker;
import managers.CollectionManager;

/**
 * Команда 'info'
 * Выводит информацию о коллекции
 */
public class InfoCommand extends Command {

    /**
     * Создает команду info
     */
    public InfoCommand() {
        super("info", "вывести информацию о коллекции",
                CommandType.WITHOUT_WORKER_DATA, CommandAccess.NO_ACCESS, false);
    }

    /**
     * Исполняет команду
     *
     * @param args              аргументы команды (не используются)
     * @param collectionManager менеджер коллекции, информацию о которой нужно вывести
     * @param username
     */
    @Override
    public String execute(String[] args, CollectionManager collectionManager, String username) {
        if (args.length > 0) {
            return "Данная команда не принимает аргументы!";
        } else {
            return collectionManager.getCollectionInfo();
        }

    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker, String username) {
        return "";
    }
}