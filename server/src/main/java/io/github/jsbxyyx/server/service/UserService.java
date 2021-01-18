package io.github.jsbxyyx.server.service;

import com.google.gson.reflect.TypeToken;
import io.github.jsbxyyx.common.GsonUtil;
import io.github.jsbxyyx.msg.ErrorCode;
import io.github.jsbxyyx.server.exception.BasicException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author
 * @since
 */
public class UserService {

    private static Map<String, User> USER_MAP;

    /**
     * key: group value: [user]
     */
    public static Map<String, List<User>> GROUP_MAP;

    public static void init() {
        InputStream in = null;
        try {
            in = UserService.class.getResourceAsStream("/user.json");
            Map<String, User> o = GsonUtil.get().fromJson(new InputStreamReader(in),
                    new TypeToken<Map<String, User>>() {
                    }.getType());
            USER_MAP = Collections.unmodifiableMap(o);

            Map<String, List<User>> map = new HashMap<>();
            for (Map.Entry<String, User> entry : o.entrySet()) {
                User value = entry.getValue();
                String group = value.getGroup();
                if (map.containsKey(group)) {
                    map.get(group).add(value);
                } else {
                    List<User> list = new ArrayList<>();
                    list.add(value);
                    map.put(group, list);
                }
            }
            GROUP_MAP = Collections.unmodifiableMap(map);
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

    public static List<User> getUserListByGroup(String group) {
        return Collections.unmodifiableList(GROUP_MAP.get(group));
    }
}
