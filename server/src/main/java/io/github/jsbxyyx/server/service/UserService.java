package io.github.jsbxyyx.server.service;

import com.google.gson.reflect.TypeToken;
import io.github.jsbxyyx.common.GsonUtil;
import io.github.jsbxyyx.msg.ErrorCode;
import io.github.jsbxyyx.server.exception.BasicException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 * @since
 */
public class UserService {

    private static Map<String, User> USER_MAP;

    /**
     * key: group value: [user]
     */
    public static Map<String, Set<User>> GROUP_MAP = new ConcurrentHashMap<>();

    public static void init() {
        InputStream in = null;
        try {
            in = UserService.class.getResourceAsStream("/user.json");
            Map<String, User> o = GsonUtil.get().fromJson(new InputStreamReader(in),
                    new TypeToken<Map<String, User>>() {
                    }.getType());
            USER_MAP = Collections.unmodifiableMap(o);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
    }

    public static User login(String username, String password) {
        User user = USER_MAP.get(username);
        if (user == null) {
            throw new BasicException(ErrorCode.USER_NOT_FOUND);
        }
        if (!Objects.equals(user.getPassword(), password)) {
            throw new BasicException(ErrorCode.USER_PASSWORD_INCORRECT);
        }
        return user;
    }

    public static Set<User> getOnlineUserByGroup(String group) {
        return Collections.unmodifiableSet(GROUP_MAP.get(group));
    }

    public static void online(String group, User findUser) {
        GROUP_MAP.putIfAbsent(group, new HashSet<>());
        GROUP_MAP.get(group).add(findUser);
    }

    public static void offline(String username) {
        User user = USER_MAP.get(username);
        if (user != null) {
            Set<User> users = GROUP_MAP.get(user.getGroup());
            if (users != null) {
                users.remove(user);
            }
        }
    }
}
