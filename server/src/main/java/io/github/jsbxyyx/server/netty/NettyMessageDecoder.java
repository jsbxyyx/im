package io.github.jsbxyyx.server.netty;

import io.github.jsbxyyx.msg.Msg;
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
        if (Constants.MAGIC[0] != b0 || Constants.MAGIC[1] != b1) {
            throw new IllegalArgumentException("magic : " + b0 + ", " + b1 + " illegal");
        }

        byte version = frame.readByte();
        int fullLength = frame.readInt();
        short headLength = frame.readShort();
        byte type = frame.readByte();
        int id = frame.readInt();

        Msg msg = new Msg();
        msg.setId(id);
        msg.setType(type);

        // direct read head with zero-copy
        int headMapLength = headLength - 2;
        if (headMapLength > 0) {
            Map<String, String> map = decodeHeadMap(frame, headMapLength);
            msg.getHeadMap().putAll(map);
        }

        // read body
        int bodyLength = fullLength - headLength;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            frame.readBytes(bs);
            bs = EncryptionFactory.get(msg.getHeadMap().get("token")).decrypt(bs);
            Object object = SerializerFactory.get().deserialize(bs);
            msg.setBody(object);
        }

        return msg;
    }

    private byte[] decrypt(byte[] bs) {
        // TODO
        return null;
    }

    private Map<String, String> decodeHeadMap(ByteBuf in, int length) {
        Map<String, String> map = new HashMap<>(16);
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
