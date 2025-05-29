import commands.*;
import managers.CollectionManager;
import managers.CommandManager;
import network.TCPServer;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Главный класс сервера, отвечающий за инициализацию и запуск серверной части приложения
 */
public class ServerMain {
    /**
     * Точка входа в серверное приложение
     * Инициализирует менеджеры коллекции и команд, регистрирует все доступные команды
     * и запускает TCP сервер
     *
     * @param args аргументы командной строки, где args[0] - путь к файлу с коллекцией
     */
    public static void main(String[] args) throws SQLException {

        CollectionManager collectionManager = new CollectionManager();
        CommandManager commandManager = new CommandManager();
        TCPServer server = new TCPServer(collectionManager, commandManager);
        collectionManager.loadCollectionFromFile();

        commandManager.registerCommand(new HelpCommand(commandManager));
        commandManager.registerCommand(new InfoCommand());
        commandManager.registerCommand(new ShowCommand());
        commandManager.registerCommand(new AddCommand());
        commandManager.registerCommand(new UpdateCommand());
        commandManager.registerCommand(new RemoveByIdCommand());
        commandManager.registerCommand(new ClearCommand());
        commandManager.registerCommand(new ExecuteScriptCommand(commandManager));
        commandManager.registerCommand(new RemoveFirstCommand());
        commandManager.registerCommand(new AddIfMinCommand());
        commandManager.registerCommand(new RemoveLowerCommand());
        commandManager.registerCommand(new SumOfSalaryCommand());
        commandManager.registerCommand(new MinByCreationDateCommand());
        commandManager.registerCommand(new PrintFieldAscendingSalaryCommand());
        commandManager.registerCommand(new ExitCommand());
        commandManager.registerCommand(new SaveCommand());

        try {
            server.start(5555);
        } catch (IOException e) {
            System.err.println("Не получилось запустить сервер: " + e.getMessage());
            System.exit(1);
        }
    }
}
