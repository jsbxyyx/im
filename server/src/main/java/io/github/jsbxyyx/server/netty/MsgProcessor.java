package io.github.jsbxyyx.server.netty;

/**
 * @author
 */
public interface MsgProcessor {

    String type();

    void handle(Msg msg);

}
