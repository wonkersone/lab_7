package commands;

import mainClasses.Worker;
import managers.CollectionManager;
import managers.CommandManager;

import java.util.Map;

/**
 * Команда 'help'
 * Выводит справку по всем доступным командам
 */
public class HelpCommand extends Command{
    private final CommandManager commandManager;

    /**
     * Создает команду help
     * @param commandManager менеджер команд, содержащий информацию о всех доступных командах
     */
    public HelpCommand(CommandManager commandManager) {
        super("help", "вывести справку по доступным командам",
                CommandType.WITHOUT_WORKER_DATA, CommandAccess.NO_ACCESS, false);
        this.commandManager = commandManager;
    }

    /**
     * Исполняет команду
     *
     * @param args              аргументы команды (не используются)
     * @param collectionManager менеджер коллекции (не используется)
     * @param username
     */
    @Override
    public String execute(String[] args, CollectionManager collectionManager, String username) {
        if (args.length > 0) {
            return "Данная команда не принимает аргументы!";
        } else {
            StringBuilder res = new StringBuilder("Доступные команды:\n");
            Map<String, Command> commands = commandManager.getCommands();
            for (Map.Entry<String, Command> entry : commands.entrySet()) {
                String key = entry.getKey();
                if (!key.equals("save")) {
                    Command command = entry.getValue();
                    res.append(key).append(" - ").append(command.getDescription()).append(";\n");
                }

            }
            return res.toString();
        }
    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker, String username) {
        return "";
    }
}
