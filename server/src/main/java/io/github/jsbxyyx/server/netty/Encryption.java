package io.github.jsbxyyx.server.netty;

/**
 * @author
 * @since
 */
public interface Encryption {

    byte[] encrypt(byte[] bytes);

    byte[] decrypt(byte[] bytes);

}
