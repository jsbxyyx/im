package io.github.jsbxyyx.msg;

/**
 * @author
 * @since
 */
public interface Serializer {

    byte[] serialize(int msgType, MsgBody msgBody);

    MsgBody deserialize(int msgType, byte[] bytes);

}
