package io.github.jsbxyyx.server.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.jsbxyyx.server.exception.BasicException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author
 * @since
 */
public class UserService {

    private static final Gson Json = new GsonBuilder().disableHtmlEscaping().create();

    private static Map<String, User> USER_MAP;

    public static void init() {
        InputStream in = null;
        try {
            in = UserService.class.getResourceAsStream("/user.json");
            Map<String, User> o = Json.fromJson(new InputStreamReader(in),
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
            throw new BasicException("0001", "user not found");
        }
        if (!Objects.equals(user.getPassword(), password)) {
            throw new BasicException("0002", "password not right");
        }
        return user;
    }
}
