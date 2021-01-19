package io.github.jsbxyyx.pcclient;

import io.github.jsbxyyx.pcclient.context.ApplicationContext;
import io.github.jsbxyyx.pcclient.service.HeartbeatService;
import io.github.jsbxyyx.pcclient.ui.FontUtil;
import io.github.jsbxyyx.pcclient.ui.LoginUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @author
 * @since
 */
public class PcClientMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(PcClientMain.class);

    public static void main(String[] args) throws Exception {
        ApplicationContext.init();

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                FontUtil.initGlobalFont(new Font("Serif", Font.PLAIN, 12));
                new LoginUI().launch();
            }
        });

        HeartbeatService.startHeartbeat();
    }

}
