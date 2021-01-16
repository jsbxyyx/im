package io.github.jsbxyyx.common;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author
 */
public class IdGenerator {

    private static final int MASK = 0x7FFFFFFF;
    private final AtomicInteger atom = new AtomicInteger(0);
    private static final IdGenerator INSTANCE = new IdGenerator();

    private IdGenerator() {
    }

    public static IdGenerator getInstance() {
        return INSTANCE;
    }

    public final int incrementAndGet() {
        return atom.incrementAndGet() & MASK;
    }

    public final int getAndIncrement() {
        return atom.getAndIncrement() & MASK;
    }

    public int get() {
        return atom.get() & MASK;
    }

}