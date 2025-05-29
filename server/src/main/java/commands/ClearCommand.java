package commands;

import mainClasses.Worker;
import managers.CollectionManager;

/**
 * Команда 'clear'
 * Очищает коллекцию
 */
public class ClearCommand extends Command {

    /**
     * Создает команду clear
     */
    public ClearCommand() {
        super("clear", "очистить коллекцию",
                CommandType.WITHOUT_WORKER_DATA, CommandAccess.WITH_ACCESS, false);
    }

    /**
     * Исполняет команду
     *
     * @param args              аргументы команды (не используются)
     * @param collectionManager менеджер коллекции, которую нужно очистить
     * @param username
     */
    @Override
    public String execute(String[] args, CollectionManager collectionManager, String username) {
        if (args.length > 0) {
            return "Данная команда не принимает аргументов!";
        } else {
            return collectionManager.clearCollection(username);
        }

    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker, String username) {
        return "";
    }
}