package io.github.jsbxyyx.pcclient.netty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 * @since
 */
public class Global {

    private static final Map<String, byte[]> TOKEN = new ConcurrentHashMap<>();
    private static String username;
    private static String token;
    private static String group;
    private static String groupName;

    public static void putKey(byte[] key) {
        TOKEN.put(username, key);
    }

    public static byte[] getKey() {
        byte[] bytes = TOKEN.get(username);
        if (bytes == null) {
            return null;
        }
        byte[] dst = new byte[bytes.length];
        System.arraycopy(bytes, 0, dst, 0, bytes.length);
        return dst;
    }

    public static void setToken(String token) {
        Global.token = token;
    }

    public static String getToken() {
        return token;
    }

    public static void setUsername(String username) {
        Global.username = username;
    }

    public static String getUsername() {
        return username;
    }

    public static void setGroup(String group) {
        Global.group = group;
    }

    public static String getGroup() {
        return group;
    }

    public static void setGroupName(String groupName) {
        Global.groupName = groupName;
    }

    public static String getGroupName() {
        return groupName;
    }

    public static void clear() {
        TOKEN.clear();
        username = null;
        token = null;
        group = null;
        groupName = null;
    }
}
