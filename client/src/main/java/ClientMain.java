import network.TCPClient;
import userManagers.UserManager;

import java.io.IOException;

/**
 * Главный класс клиентского приложения
 * Отвечает за подключение к серверу и запуск интерактивного режима работы с пользователем
 */
public class ClientMain {
    /**
     * Точка входа в клиентское приложение
     * Устанавливает соединение с сервером и запускает интерактивный режим работы
     *
     * @param args аргументы командной строки (не используются)
     * @throws Exception если возникли проблемы при работе приложения
     */
    public static void main(String[] args) throws Exception {
        TCPClient client = new TCPClient();
        try {
            client.connect("localhost", 5555);

            UserManager scanner = new UserManager(client);
            scanner.startInteractiveMode(scanner.getCurrentUser());

        } catch (IOException e) {
            System.err.println("Ошибка подключения к серверу: " + e.getMessage());
            System.exit(1);
        } catch (Exception e1) {
            System.err.println("Произошла непредвиденная ошибка: " + e1.getMessage());
            System.exit(1);
        }
    }
}
