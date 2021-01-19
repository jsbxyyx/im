package io.github.jsbxyyx.pcclient.service;

import io.github.jsbxyyx.msg.HeartbeatMsg;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.pcclient.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author
 * @since
 */
public class HeartbeatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatService.class);

    private static ScheduledThreadPoolExecutor HEARTBEAT_POOL = new ScheduledThreadPoolExecutor(1);

    private static final ThreadPoolExecutor CONNECT_POOL = new ThreadPoolExecutor(1, 1,
            60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024),
            new ThreadPoolExecutor.DiscardPolicy());

    public static void startHeartbeat() {
        HEARTBEAT_POOL.shutdownNow();
        HEARTBEAT_POOL = new ScheduledThreadPoolExecutor(1);
        HEARTBEAT_POOL.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    HeartbeatMsg msg = new HeartbeatMsg();
                    msg.setPing(true);
                    Msg rsp = ApplicationContext.sendSync(msg);
                    HeartbeatMsg body = (HeartbeatMsg) rsp.getBody();
                    LOGGER.debug("ping : {}", body.isPing());
                    ApplicationContext.setNetworkStatus(true);
                } catch (Exception e) {
                    reconnect();
                    LOGGER.error("heartbeat failed.", e);
                }
            }
        }, 0, 3 * 60 * 1000, TimeUnit.MILLISECONDS);
    }

    public static void reconnect() {
        CONNECT_POOL.submit(new ConnectTask());
    }

    private static class ConnectTask implements Runnable {
        @Override
        public void run() {
            try {
                ApplicationContext.connect();
                LOGGER.info("connected...");
                long taskCount = CONNECT_POOL.getTaskCount();
                if (taskCount > 0) {
                    CONNECT_POOL.getQueue().drainTo(new ArrayList<>((int) taskCount));
                }
                ApplicationContext.setNetworkStatus(true);
            } catch (Exception e) {
                ApplicationContext.setNetworkStatus(false);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                }
                CONNECT_POOL.submit(new ConnectTask());
                LOGGER.info("ex reconnect...");
            }
        }
    }

}
