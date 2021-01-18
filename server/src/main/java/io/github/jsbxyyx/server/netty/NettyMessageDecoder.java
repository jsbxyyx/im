package io.github.jsbxyyx.server.netty;

import io.github.jsbxyyx.common.Constants;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.MsgBody;
import io.github.jsbxyyx.msg.MsgType;
import io.github.jsbxyyx.msg.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyMessageDecoder.class);

    public NettyMessageDecoder() {
        super(1 * 1024 * 1024 * 1024, 3, 4,
                -7, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            try {
                return decoded(frame);
            } catch (Exception e) {
                LOGGER.error("Decode frame error!", e);
                throw e;
            } finally {
                frame.release();
            }
        }
        return decoded;
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
        int headMapLength = headLength - Constants.HEAD_LENGTH;
        if (headMapLength > 0) {
            Map<String, String> map = decodeHeadMap(frame, headMapLength);
            msg.getHeadMap().putAll(map);
        }

        // read body
        int bodyLength = fullLength - headLength;
        if (bodyLength > 0) {
            byte[] body = new byte[bodyLength];
            frame.readBytes(body);

            ByteBuffer buffer = ByteBuffer.wrap(body);
            int msgType = buffer.getInt();
            byte[] content = new byte[buffer.remaining()];
            buffer.get(content);
            if (msgType == MsgType.Text) {
                content = EncryptionFactory.getDecrypt(msg.getToken()).decrypt(content);
            }
            MsgBody object = SerializerFactory.get().deserialize(msgType, content);
            msg.setBody(object);
        }

        return msg;
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
