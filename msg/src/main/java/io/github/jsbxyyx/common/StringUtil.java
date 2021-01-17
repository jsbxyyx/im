package io.github.jsbxyyx.common;

import java.util.Random;

/**
 * @author
 * @since
 */
public class StringUtil {

    public static final byte[] TABLE = new byte[62];
    private static final Random r = new Random();

    static {
        int idx = 0;
        char i = 48;
        while (i <= 57) {
            TABLE[idx++] = (byte) i;
            i++;
        }
        i = 65;
        while (i <= 90) {
            TABLE[idx++] = (byte) i;
            i++;
        }
        i = 97;
        while (i <= 122) {
            TABLE[idx++] = (byte) i;
            i++;
        }
    }

    public static byte[] generateByteArray(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length:[" + length + "] is illegal");
        }
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = TABLE[r.nextInt(TABLE.length)];
        }
        return bytes;
    }

    public static boolean isBlank(String text) {
        return text == null || "".equals(text.trim());
    }
}
