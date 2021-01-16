package io.github.jsbxyyx.msg.processor;

import io.github.jsbxyyx.msg.Msg;

/**
 * @author
 */
public interface MsgProcessor {

    byte type();

    Msg handle(Msg msg);

}
