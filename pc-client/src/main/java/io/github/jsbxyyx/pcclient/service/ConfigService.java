package io.github.jsbxyyx.pcclient.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;

/**
 * @author
 * @since
 */
public class ConfigService {

    private static final Gson Json = new GsonBuilder().disableHtmlEscaping().create();

    private static Map<String, String> CONFIG;

    public static void init() {
        InputStream in = null;
        try {
            in = ConfigService.class.getResourceAsStream("/server.json");
            Map<String, String> o = Json.fromJson(new InputStreamReader(in),
                    new TypeToken<Map<String, String>>() {
                    }.getType());
            CONFIG = Collections.unmodifiableMap(o);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
    }

    public static String getValue(String key) {
        return CONFIG.get(key);
    }
}
