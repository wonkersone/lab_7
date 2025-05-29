package commands;

import mainClasses.Worker;
import managers.CollectionManager;

/**
 * Команда 'add'
 * Добавляет новый элемент в коллекцию
 */
public class AddCommand extends Command {

    /**
     * Создает команду add
     */
    public AddCommand() {
        super("add", "добавить новый элемент в коллекцию",
                CommandType.WITH_WORKER_DATA, CommandAccess.NO_ACCESS, false);
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
        collectionManager.addElement(worker);
        return "Работник добавлен в коллекцию";
    }

    /**
     * Выполняет команду с заданными аргументами
     *
     * @param args              аргументы команды
     * @param collectionManager менеджер коллекции, над которой выполняется команда
     */
}