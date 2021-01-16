package io.github.jsbxyyx.server.netty;

/**
 * @author
 * @since
 */
public class SerializerFactory {

    public static Serializer get() {
        return new Serializer() {
            @Override
            public <T> byte[] serialize(T t) {
                return new byte[0];
            }

            @Override
            public <T> T deserialize(byte[] bytes) {
                return null;
            }
        };
    }

}
