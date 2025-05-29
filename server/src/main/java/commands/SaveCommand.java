package commands;

import mainClasses.Worker;
import managers.CollectionManager;

/**
 * Команда 'save'
 * Сохраняет коллекцию в файл
 */
public class SaveCommand extends Command {

    /**
     * Создает команду save
     */
    public SaveCommand() {
        super("save", "сохранить коллекцию в файл", CommandType.WITHOUT_WORKER_DATA, CommandAccess.NO_ACCESS, false);
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
            return "Данная команда не принимает аргументы!";
        } else {
            try {
                collectionManager.saveCollectionToFile();
                return "Коллекция успешно сохранена.";
            } catch (Exception e) {
                return "Ошибка при сохранении коллекции: " + e.getMessage();
            }
        }

    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker, String username) {
        return "";
    }
}