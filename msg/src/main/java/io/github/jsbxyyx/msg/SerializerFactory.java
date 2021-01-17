package io.github.jsbxyyx.msg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.StandardCharsets;

/**
 * @author
 * @since
 */
public class SerializerFactory {

    private static final Gson Json = new GsonBuilder().disableHtmlEscaping().create();

    public static Serializer get() {
        return new Serializer() {
            @Override
            public byte[] serialize(int msgType, MsgBody msgBody) {
                return Json.toJson(msgBody).getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public MsgBody deserialize(int msgType, byte[] bytes) {
                Class aClass = MsgBodyFactory.get(msgType);
                return (MsgBody) Json.fromJson(new String(bytes), aClass);
            }
        };
    }

}
