package network;

import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Security {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String HASH_ALGORITHM = "SHA-384";
    private static final int SALT_LENGTH = 16;

    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) {
        try {
            // Создаем экземпляр MessageDigest с алгоритмом SHA-384
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);

            // Объединяем пароль и соль
            String passwordWithSalt = password + salt;

            // Хешируем
            byte[] hashedBytes = digest.digest(passwordWithSalt.getBytes());

            // Конвертируем в строку Base64
            return Base64.getEncoder().encodeToString(hashedBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Не удалось найти алгоритм хеширования: " + HASH_ALGORITHM, e);
        }
    }
}
