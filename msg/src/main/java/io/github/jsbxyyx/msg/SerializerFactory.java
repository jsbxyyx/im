package io.github.jsbxyyx.msg;

import io.github.jsbxyyx.common.GsonUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author
 * @since
 */
public class SerializerFactory {

    public static Serializer get() {
        return new Serializer() {
            @Override
            public byte[] serialize(int msgType, MsgBody msgBody) {
                return GsonUtil.get().toJson(msgBody).getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public MsgBody deserialize(int msgType, byte[] bytes) {
                Class aClass = MsgBodyFactory.get(msgType);
                return (MsgBody) GsonUtil.get().fromJson(new String(bytes), aClass);
            }
        };
    }

}
