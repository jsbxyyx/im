package io.github.jsbxyyx.pcclient.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.InputStream;
import java.util.Enumeration;

/**
 * @author
 */
public class FontUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FontUtil.class);

    public static void initGlobalFont(String path) {
        try (InputStream in = FontUtil.class.getResourceAsStream(path)) {
            Font font = Font.createFont(Font.TRUETYPE_FONT, in);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            font = font.deriveFont(Font.TRUETYPE_FONT, 14);
            initGlobalFont(font);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void initGlobalFont(Font font) {
        FontUIResource fontResource = new FontUIResource(font);
        for(Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if(value instanceof FontUIResource) {
                UIManager.put(key, fontResource);
            }
        }
    }

}
