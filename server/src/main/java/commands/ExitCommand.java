package commands;

import mainClasses.Worker;
import managers.CollectionManager;

/**
 * Команда 'exit'
 * Завершает программу
 */
public class ExitCommand extends Command {

    /**
     * Создает команду exit
     */
    public ExitCommand() {
        super("exit", "завершить программу", CommandType.WITHOUT_WORKER_DATA, CommandAccess.NO_ACCESS, false);
    }

    /**
     * Исполняет команду
     *
     * @param args              аргументы команды (не используются)
     * @param collectionManager менеджер коллекции (не используется)
     * @param username
     */
    @Override
    public String  execute(String[] args, CollectionManager collectionManager, String username) {
        if (args.length > 0) {
            return "Данная команда не принимает аргументов!";
        } else {
            System.exit(0);
        }
        return "";
    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker, String username) {
        return "";
    }
}