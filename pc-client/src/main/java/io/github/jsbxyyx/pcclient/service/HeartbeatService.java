package io.github.jsbxyyx.pcclient.service;

import io.github.jsbxyyx.msg.HeartbeatMsg;
import io.github.jsbxyyx.pcclient.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author
 * @since
 */
public class HeartbeatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatService.class);

    private static final ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(1);

    public static void startHeartbeat() {
        pool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    HeartbeatMsg msg = new HeartbeatMsg();
                    msg.setPing(true);
                    ApplicationContext.sendAsync(msg);
                } catch (Exception e) {
                    LOGGER.error("heartbeat failed.", e);
                }
            }
        }, 0, 5 * 60 * 1000, TimeUnit.MILLISECONDS);
    }
}
