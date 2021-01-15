package io.github.jsbxyyx.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 */
public class NettyMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decoded = decoded(in);
        if (decoded != null) {
            out.add(decoded);
        }
    }

    private Object decoded(ByteBuf frame) {
        byte b0 = frame.readByte();
        byte b1 = frame.readByte();
        if (0xca != b0 || 0xca != b1) {
            throw new IllegalArgumentException("Unknown magic code: " + b0 + ", " + b1);
        }

        byte version = frame.readByte();
        int fullLength = frame.readInt();
        short headLength = frame.readShort();
        byte type = frame.readByte();
        int id = frame.readInt();



        Msg msg = MsgFactory.create(type);
        msg.setId(id);
        msg.setType(type);

        // direct read head with zero-copy
        int headMapLength = headLength - 2;
        if (headMapLength > 0) {
            Map<String, String> map = decodeHeadMap(frame, headMapLength);
            msg.getHeadMap().putAll(map);
        }

        // read body
        if (type == HeartbeatMsg.TYPE) {
            msg.setBody(new HeartbeatMsg());
        } else {
            int bodyLength = fullLength - headLength;
            if (bodyLength > 0) {
                byte[] bs = new byte[bodyLength];
                frame.readBytes(bs);
                msg.setBody(msg.decode(decrypt(bs)));
            }
        }

        return msg;
    }

    private byte[] decrypt(byte[] bs) {
        // TODO
        return null;
    }

    private Map<String, String> decodeHeadMap(ByteBuf in, int length) {
        Map<String, String> map = new HashMap<>();
        if (in == null || in.readableBytes() == 0 || length == 0) {
            return map;
        }
        int cur = in.readerIndex();
        while (in.readerIndex() - cur < length) {
            String key = readString(in);
            String value = readString(in);
            map.put(key, value);
        }
        return map;
    }

    private String readString(ByteBuf in) {
        int length = in.readShort();
        if (length < 0) {
            return null;
        } else if (length == 0) {
            return "";
        } else {
            byte[] value = new byte[length];
            in.readBytes(value);
            return new String(value, StandardCharsets.UTF_8);
        }
    }

}
