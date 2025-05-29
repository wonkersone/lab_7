package network;

import shit.Request;
import shit.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Класс, реализующий TCP клиент для взаимодействия с сервером
 * Обеспечивает установку соединения, отправку запросов и получение ответов
 */
public class TCPClient {
    /** Сокет для связи с сервером */
    private Socket clientSocket;
    /** Поток для отправки объектов на сервер */
    private ObjectOutputStream out;
    /** Поток для получения объектов от сервера */
    private ObjectInputStream in;

    /**
     * Устанавливает соединение с сервером
     *
     * @param ip IP-адрес сервера
     * @param port порт сервера
     * @throws IOException если произошла ошибка при установке соединения
     */
    public void connect(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(clientSocket.getInputStream());
        System.out.println("Подключено к серверу " + ip + ":" + port);
    }

    /**
     * Отправляет запрос на сервер и получает ответ
     * Метод синхронизирован для обеспечения потокобезопасности
     *
     * @param request запрос для отправки
     * @return ответ от сервера
     * @throws IOException если произошла ошибка при обмене данными
     */
    public Response sendRequest(Request request) throws IOException {
        try {
            synchronized (out) {  // Синхронизация для потокобезопасности
                out.writeObject(request);
                out.flush();
                return (Response) in.readObject();
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Ошибка десериализации", e);
        } catch (IOException e) {
            // При разрыве соединения пробуем переподключиться
            reconnect();
            throw e;
        }
    }

    /**
     * Переподключается к серверу в случае разрыва соединения
     *
     * @throws IOException если не удалось переподключиться
     */
    private void reconnect() throws IOException {
        disconnect();
        connect("localhost", 8000); // Или параметры из конфига
    }

    /**
     * Закрывает соединение с сервером
     * Освобождает все использованные ресурсы
     *
     * @throws IOException если произошла ошибка при закрытии соединения
     */
    public void disconnect() throws IOException {
        try {
            out.close();
            in.close();
        } finally {
            clientSocket.close();
        }
    }

    public ObjectInputStream getInputStream() {
        return in;
    }

    public ObjectOutputStream getOutputStream() {
        return out;
    }
}