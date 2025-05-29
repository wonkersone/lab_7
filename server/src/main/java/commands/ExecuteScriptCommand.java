package commands;

import mainClasses.Worker;
import managers.CollectionManager;
import managers.CommandManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Команда 'execute_script'
 * Считывает и исполняет скрипт из указанного файла
 */
public class ExecuteScriptCommand extends Command {
    private final CommandManager commandManager;
    private static final Set<String> executedScripts = new HashSet<>();

    /**
     * Создает команду execute_script
     * @param commandManager менеджер команд для выполнения команд из скрипта
     */
    public ExecuteScriptCommand(CommandManager commandManager) {
        super("execute_script", "считать и исполнить скрипт из указанного файла",
                CommandType.WITH_SCRIPT_FILE, CommandAccess.NO_ACCESS, true);
        this.commandManager = commandManager;
    }

    /**
     * Исполняет команду
     *
     * @param args              аргументы команды (путь к файлу скрипта)
     * @param collectionManager менеджер коллекции
     * @param username
     */
    @Override
    public String execute(String[] args, CollectionManager collectionManager, String username) {
        // Логика перенесена в TCPServer.processScriptRequest
        return "Скрипт обработан";
    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker, String username) {
        return "";
    }
}