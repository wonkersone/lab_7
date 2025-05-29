package commands;

import mainClasses.Worker;
import managers.CollectionManager;

import java.util.Comparator;

/**
 * Команда 'min_by_creation_date'
 * Выводит любой объект из коллекции, значение поля creationDate которого является минимальным
 */
public class MinByCreationDateCommand extends Command {

    /**
     * Создает команду min_by_creation_date
     */
    public MinByCreationDateCommand() {
        super("min_by_creation_date", "вывести объект из коллекции, значение поля creationDate которого " +
                "является минимальным", CommandType.WITHOUT_WORKER_DATA, CommandAccess.NO_ACCESS, false);
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
        if (args.length > 0 ) {
            return "Данная команда не принимает аргументы!";
        } else {
            String res =  collectionManager.getWorkersCollection().stream()
                            .min(Comparator.comparing(Worker::getCreationDate)).toString();
            return res.substring(9, res.length()-1);
        }

    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker, String username) {
        return "";
    }
}