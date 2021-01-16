package io.github.jsbxyyx.server.msg.processor;

import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.TextMsg;
import io.github.jsbxyyx.msg.processor.MsgProcessor;

import java.util.Date;

/**
 * @author
 * @since
 */
public class TextMsgProcessor implements MsgProcessor {
    @Override
    public byte type() {
        return TextMsg.TYPE;
    }

    @Override
    public Msg handle(Msg msg) {

        TextMsg tm = (TextMsg) msg.getBody();
        String from = tm.getFrom();
        String to = tm.getTo();
        String type = tm.getType();
        String text = tm.getText();
        Date createTime = tm.getCreateTime();


        return null;
    }
}
