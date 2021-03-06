package io.github.jsbxyyx.server.netty;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 * @since
 */
public class Global {

    private static final Map<String, byte[]> TOKEN = new ConcurrentHashMap<>();
    private static final Map<String, String> USER_CHANNEL_ID_MAP = new ConcurrentHashMap<>();

    public static void putToken(String username, byte[] key) {
        TOKEN.put(username, key);
    }

    public static byte[] getKey(String username) {
        byte[] bytes = TOKEN.get(username);
        if (bytes == null) {
            return null;
        }
        byte[] dst = new byte[bytes.length];
        System.arraycopy(bytes, 0, dst, 0, bytes.length);
        return dst;
    }

    public static void putUserChannel(String username, String channelId) {
        USER_CHANNEL_ID_MAP.put(username, channelId);
    }

    public static String getChannelId(String username) {
        return USER_CHANNEL_ID_MAP.get(username);
    }

    public static String getUsername(String channelId) {
        for (Map.Entry<String, String> entry : USER_CHANNEL_ID_MAP.entrySet()) {
            if (Objects.equals(entry.getValue(), channelId)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
