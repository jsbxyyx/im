package io.github.jsbxyyx.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author
 */
public class HeartbeatMsg extends Msg {

    public static final int TYPE = 0;

    @Override
    public byte[] encode() {
        ByteBuf out = Unpooled.buffer(1024);
        out.writeBoolean(true);

        byte[] dst = new byte[out.readableBytes()];
        out.readBytes(dst);
        return dst;
    }

}
