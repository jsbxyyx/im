package io.github.jsbxyyx.server.netty;

import io.github.jsbxyyx.common.AESUtil;

/**
 * @author
 * @since
 */
public class EncryptionFactory {

    public static Encryption get(String token) {
        final byte[] key = Global.get(token);
        return new Encryption() {
            @Override
            public byte[] encrypt(byte[] bytes) {
                return AESUtil.encrypt(key, bytes);
            }

            @Override
            public byte[] decrypt(byte[] bytes) {
                return AESUtil.decrypt(key, bytes);
            }
        };
    }

}
