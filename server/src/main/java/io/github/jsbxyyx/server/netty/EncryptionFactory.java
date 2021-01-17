package io.github.jsbxyyx.server.netty;

import io.github.jsbxyyx.common.AESUtil;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * @author
 * @since
 */
public class EncryptionFactory {

    private static final byte[] KEY = "0000000000000000".getBytes(StandardCharsets.UTF_8);
    private static final String SPLIT = "#_#";

    public static Encryption getEncrypt(String username) {
        final byte[] key = Global.getKey(username);
        return new Encryption() {
            @Override
            public byte[] encrypt(byte[] bytes) {
                return AESUtil.encrypt(key, bytes);
            }
        };
    }

    public static Decryption getDecrypt(String token) {
        String[] strings = decodeToken(token);
        final byte[] key = Global.getKey(strings[1]);
        return new Decryption() {
            @Override
            public byte[] decrypt(byte[] bytes) {
                return AESUtil.encrypt(key, bytes);
            }
        };
    }


    public static String[] decodeToken(String token) {
        byte[] decode = Base64.getDecoder().decode(token);
        String s = new String(AESUtil.decrypt(KEY, decode));
        return s.split(SPLIT);
    }

    public static String encodeToken(String username) {
        String s = UUID.randomUUID().toString() + SPLIT + username;
        byte[] encrypt = AESUtil.encrypt(KEY, s.getBytes());
        return Base64.getEncoder().encodeToString(encrypt);
    }

}
