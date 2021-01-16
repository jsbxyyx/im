package io.github.jsbxyyx.server.netty;

/**
 * @author
 * @since
 */
public class Constants {

    public static final int FULL_LENGTH = 14;
    public static final int HEAD_LENGTH = 2;
    public static final byte VERSION = 1;
    public static final byte[] MAGIC = new byte[] {(byte) 0xca, (byte) 0xca};
}
