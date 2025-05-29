package network;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHConnection implements Runnable {
    // Параметры SSH-подключения
    private final String sshUser;      // Логин на сервере
    private final String sshHost;      // Адрес сервера (se.ifmo.ru)
    private final int sshPort;         // Порт SSH
    private final String sshPassword;  // Пароль от сервера

    // Параметры туннелирования
    private final int localPort;       // Локальный порт для проброса
    private final String dbHost;       // Адрес БД за SSH (pg)
    private final int dbPort;          // Порт БД (5432)

    private Session session;
    private boolean isConnected = false;

    public SSHConnection(String sshUser, String sshPassword, String sshHost, int sshPort,
                         int localPort, String dbHost, int dbPort) {
        this.sshUser = sshUser;
        this.sshPassword = sshPassword;
        this.sshHost = sshHost;
        this.sshPort = sshPort;
        this.localPort = localPort;
        this.dbHost = dbHost;
        this.dbPort = dbPort;
    }

    @Override
    public void run() {
        try {
            JSch jsch = new JSch();

            session = jsch.getSession(sshUser, sshHost, sshPort);
            session.setPassword(sshPassword);

            session.setConfig("StrictHostKeyChecking", "no");

            session.connect();

            session.setPortForwardingL(localPort, dbHost, dbPort);

            System.out.printf("SSH-туннель создан: localhost:%d -> %s:%d через %s%n",
                    localPort, dbHost, dbPort, sshHost);

            isConnected = true;

            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1000);
            }

        } catch (JSchException | InterruptedException e) {
            System.err.println("Ошибка SSH-туннеля: " + e.getMessage());
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public boolean waitUntilConnected(long timeoutMs) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while (!isConnected && (System.currentTimeMillis() - startTime) < timeoutMs) {
            Thread.sleep(100);
        }
        return isConnected;
    }

    public void close() {
        if (session != null && session.isConnected()) {
            session.disconnect();
            System.out.println("SSH-туннель закрыт");
        }
        isConnected = false;
    }
}