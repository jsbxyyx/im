package io.github.jsbxyyx.server.netty;

/**
 * @author
 * @since
 */
public interface Serializer {

    <T> byte[] serialize(T t);

    <T> T deserialize(byte[] bytes);

}
