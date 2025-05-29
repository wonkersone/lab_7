package userManagers;

import network.TCPClient;
import shit.Request;
import shit.Response;
import shit.User;

import java.io.IOException;
import java.util.Scanner;

public class UserAuthorization {
    Scanner scanner;

    public UserAuthorization() {
        this.scanner = new Scanner(System.in);
    }

    public User authentication(Scanner scanner, TCPClient client) throws IOException {
        System.out.println("Чтобы продолжить работу с приложением, необходимо авторизоваться" +
                "\nВведите '0' чтобы создать новый аккаунт" +
                "\nВведите '1' чтобы зайти в существующий");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();
        if (choice.equals("0")) {
            System.out.print("Введите имя: ");
            String userName = scanner.nextLine().trim();
            String password;
            while (true) {
                System.out.print("Введите пароль: ");
                String password1 = scanner.nextLine().trim();
                System.out.print("Повторите пароль: ");
                String password2 = scanner.nextLine().trim();
                if (password1.equals(password2)) {
                    password = password1;
                    break;
                } else {
                    System.out.println("Пароли не совпадают. Повторите ввод!");
                }
            }
            System.out.println("Создаем аккаунт...");
            Response response = client.sendRequest(new Request(userName, password, Request.RequestType.REGISTER_NEW_USER));

            System.out.println(response.getMessage());
            return new User(userName, password);

        } else {
            Response response = new Response(false);

            while (!response.isSuccess()) {
                System.out.print("Введите имя пользователя: ");
                String userName = scanner.nextLine().trim();
                System.out.print("Введите пароль: ");
                String password = scanner.nextLine().trim();
                response = client.sendRequest(new Request(userName, password, Request.RequestType.AUTHORIZE_USER));
                if (response.isSuccess()) {
                    System.out.println(response.getMessage());
                    return new User(userName, password);
                } else {
                    System.out.println("Неверные имя пользователя или пароль! \nПовторите ввод.");
                }
            }
        }
        return null;
    }
}
