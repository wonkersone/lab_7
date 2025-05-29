package commands;

import mainClasses.Worker;
import managers.CollectionManager;

import java.util.stream.Collectors;

/**
 * Команда 'print_field_ascending_salary'
 * Выводит значения поля salary всех элементов в порядке возрастания
 */
public class PrintFieldAscendingSalaryCommand extends Command {

    /**
     * Создает команду print_field_ascending_salary
     */
    public PrintFieldAscendingSalaryCommand() {
        super("print_field_ascending_salary", "вывести значения поля salary всех элементов " +
                "в порядке возрастания", CommandType.WITHOUT_WORKER_DATA,CommandAccess.NO_ACCESS, false);
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
            String res = collectionManager.getWorkersCollection().stream()
                    .mapToLong(Worker::getSalary).sorted()
                    .mapToObj(String::valueOf).collect(Collectors.joining(", "));
            return res;
        }
    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker, String username) {
        return "";
    }
}