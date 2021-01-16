package io.github.jsbxyyx.server.netty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 * @since
 */
public class Global {

    private static final Map<String, byte[]> TOKEN = new ConcurrentHashMap<>();

    public static void put(String token, byte[] key) {
        TOKEN.put(token, key);
    }

    public static byte[] get(String token) {
        byte[] bytes = TOKEN.get(token);
        if (bytes == null) {
            return null;
        }
        byte[] dst = new byte[bytes.length];
        System.arraycopy(bytes, 0, dst, 0, bytes.length);
        return dst;
    }

}
