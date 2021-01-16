package io.github.jsbxyyx.common;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author
 * @since
 */
public class AESUtil {

    public static byte[] encrypt(byte[] key, byte[] bytes) {
        if (key.length != 16) {
            throw new IllegalArgumentException("secretKey length (must be 16 bytes)");
        }
        try {
            Cipher cipher = init(key, Cipher.ENCRYPT_MODE);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decrypt(byte[] secretKey, byte[] bytes) {
        if (secretKey.length != 16) {
            throw new RuntimeException("secretKey length (must be 16 bytes)");
        }
        try {
            Cipher cipher = init(secretKey, Cipher.DECRYPT_MODE);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Cipher init(byte[] raw, int mode) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException {
        // 防止Linux下生成随机key
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(raw);
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128, secureRandom);
        SecretKeySpec key = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(raw);
        cipher.init(mode, key, iv);
        return cipher;
    }

}
