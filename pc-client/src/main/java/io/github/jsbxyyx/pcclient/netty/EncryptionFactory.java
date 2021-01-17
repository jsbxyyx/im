package io.github.jsbxyyx.pcclient.netty;

import io.github.jsbxyyx.common.AESUtil;

/**
 * @author
 * @since
 */
public class EncryptionFactory {

    public static Encryption getEncrypt() {
        final byte[] key = Global.getKey();
        return new Encryption() {
            @Override
            public byte[] encrypt(byte[] bytes) {
                return AESUtil.encrypt(key, bytes);
            }
        };
    }

    public static Decryption getDecrypt() {
        final byte[] key = Global.getKey();
        return new Decryption() {
            @Override
            public byte[] decrypt(byte[] bytes) {
                return AESUtil.encrypt(key, bytes);
            }
        };
    }

}
