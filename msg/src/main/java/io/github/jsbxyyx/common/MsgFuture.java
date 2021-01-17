package io.github.jsbxyyx.common;

import io.github.jsbxyyx.msg.Msg;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author
 * @since
 */
public class MsgFuture {

    private Msg msg;
    private long timeout;
    private long start = System.currentTimeMillis();
    private transient CompletableFuture<Object> origin = new CompletableFuture<>();

    public boolean isTimeout() {
        return System.currentTimeMillis() - start > timeout;
    }

    public Object get(long timeout, TimeUnit unit) throws TimeoutException,
            InterruptedException {
        Object result = null;
        try {
            result = origin.get(timeout, unit);
        } catch (ExecutionException e) {
            throw new RuntimeException("not get results in a multi-threaded environment", e);
        } catch (TimeoutException e) {
            throw new TimeoutException("cost " + (System.currentTimeMillis() - start) + " ms");
        }

        if (result instanceof RuntimeException) {
            throw (RuntimeException)result;
        } else if (result instanceof Throwable) {
            throw new RuntimeException((Throwable)result);
        }

        return result;
    }

    public void setResultMsg(Object obj) {
        origin.complete(obj);
    }

    public Msg getMsg() {
        return msg;
    }

    public void setMsg(Msg msg) {
        this.msg = msg;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

}
