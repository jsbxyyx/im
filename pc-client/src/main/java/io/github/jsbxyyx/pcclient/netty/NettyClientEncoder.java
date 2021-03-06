package io.github.jsbxyyx.pcclient.netty;

import io.github.jsbxyyx.common.Constants;
import io.github.jsbxyyx.common.RemotingUtil;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.MsgType;
import io.github.jsbxyyx.msg.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author
 */
public class NettyClientEncoder extends MessageToByteEncoder<Msg> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Msg msg, ByteBuf out) throws Exception {
        try {
            int fullLength = Constants.FULL_LENGTH;
            int headLength = Constants.HEAD_LENGTH;

            out.writeBytes(Constants.MAGIC);
            out.writeByte(Constants.VERSION);

            byte type = msg.getType();
            // full Length(4B) and head length(2B) will fix in the end.
            out.writerIndex(out.writerIndex() + 6);
            out.writeByte(type);
            out.writeInt(msg.getId());

            // direct write head with zero-copy
            Map<String, String> headMap = msg.getHeadMap();
            if (headMap != null && !headMap.isEmpty()) {
                int headMapBytesLength = encodeHeadMap(headMap, out);
                headLength += headMapBytesLength;
                fullLength += headMapBytesLength;
            }
            byte[] bodyBytes = null;
            if (msg.getBody() != null) {
                int msgType = msg.getBody().getMsgType();
                out.writeInt(msgType);
                fullLength += 4;
                bodyBytes = SerializerFactory.get().serialize(msgType, msg.getBody());
                if (MsgType.encrypt(msgType)) {
                    bodyBytes = EncryptionFactory.getEncrypt().encrypt(bodyBytes);
                }
                fullLength += bodyBytes.length;
            }

            if (bodyBytes != null) {
                out.writeBytes(bodyBytes);
            }

            // fix fullLength and headLength
            int writeIndex = out.writerIndex();
            // skip magic code(2B) + version(1B)
            out.writerIndex(writeIndex - fullLength + 3);
            out.writeInt(fullLength);
            out.writeShort(headLength);
            out.writerIndex(writeIndex);
        } catch (Exception e) {
            LOGGER.error("encode exception, " + RemotingUtil.parseChannelRemoteAddr(ctx.channel()), e);
            if (msg != null) {
                LOGGER.error(msg.toString());
            }
        }
    }

    private int encodeHeadMap(Map<String, String> map, ByteBuf out) {
        if (map == null || map.isEmpty() || out == null) {
            return 0;
        }
        int start = out.writerIndex();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null) {
                writeString(out, key);
                writeString(out, value);
            }
        }
        return out.writerIndex() - start;
    }

    private void writeString(ByteBuf out, String str) {
        if (str == null) {
            out.writeShort(-1);
        } else if (str.isEmpty()) {
            out.writeShort(0);
        } else {
            byte[] bs = str.getBytes(StandardCharsets.UTF_8);
            out.writeShort(bs.length);
            out.writeBytes(bs);
        }
    }


}
