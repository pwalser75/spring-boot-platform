package ch.frostnova.spring.boot.platform.core.logging.performance;

import ch.frostnova.spring.boot.platform.core.scope.CheckedSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.Collectors.joining;

public class PerformanceLoggingContext {

    private static final Logger log = LoggerFactory.getLogger(PerformanceLoggingAspect.class);

    /**
     * Threshold (in nanoseconds) under which calls are no longer reported in detail.
     */
    private static final long DETAIL_THRESHOLD_NS = 1_000_000; // 1 ms
    private final static ThreadLocal<PerformanceLoggingContext> current = new ThreadLocal<>();

    private final Deque<InvocationInfo> invocations = new LinkedList<>();
    private final Deque<InvocationInfo> invocationStack = new LinkedList<>();
    private final Deque<AtomicLong> nestedTime = new LinkedList<>();

    public static PerformanceLoggingContext current() {
        PerformanceLoggingContext context = current.get();
        if (context == null) {
            context = new PerformanceLoggingContext();
            current.set(context);
        }
        return context;
    }

    boolean isIntermediateInvocation() {
        return invocationStack.size() > 0;
    }

    private void enter(String invocation) {
        long time = System.nanoTime();
        InvocationInfo invocationInfo = new InvocationInfo(invocationStack.size(), invocation, time);
        invocations.add(invocationInfo);
        invocationStack.push(invocationInfo);
        nestedTime.push(new AtomicLong());
    }

    private void exit(Throwable t) {
        long time = System.nanoTime();
        if (invocationStack.isEmpty()) {
            throw new IllegalStateException("No invocation in progress");
        }

        InvocationInfo info = invocationStack.pop();
        info.done(time, t != null ? t.getClass().getName() : null);
        info.setNestedTimeNs(nestedTime.pop().get());

        Optional.ofNullable(nestedTime.peek()).ifPresent(x -> x.addAndGet(info.getElapsedTimeNs()));

        if (invocationStack.isEmpty()) {

            InvocationInfo previous = null;
            Iterator<InvocationInfo> iterator = invocations.iterator();
            while (iterator.hasNext()) {
                InvocationInfo invocationInfo = iterator.next();
                if (previous != null && previous.merge(invocationInfo)) {
                    iterator.remove();
                } else {
                    previous = invocationInfo;
                }
            }
            iterator = invocations.iterator();
            InvocationInfo skipFromLevel = null;
            while (iterator.hasNext()) {
                InvocationInfo invocationInfo = iterator.next();
                if (skipFromLevel != null) {
                    if (invocationInfo.getLevel() > skipFromLevel.getLevel()) {
                        iterator.remove();
                    } else {
                        skipFromLevel = null;
                    }
                }
                if (skipFromLevel == null && invocationInfo.getElapsedTimeNs() < DETAIL_THRESHOLD_NS && invocationInfo.getLevel() > 1) {
                    skipFromLevel = invocationInfo;
                }
            }

            log.info(invocations.stream()
                    .map(InvocationInfo::toString)
                    .collect(joining("\n")));
            invocations.clear();
            current.remove();
        }
    }

    /**
     * Run code inside the performance logging context
     *
     * @param <T>            return type
     * @param invocationInfo invocationInfo
     * @param supplier       supplier to execute, required
     */
    public <T> T execute(String invocationInfo, CheckedSupplier<T> supplier) {
        enter(invocationInfo);

        Throwable error = null;
        try {
            return supplier.supply();
        } catch (RuntimeException ex) {
            error = ex;
            throw ex;
        } catch (Throwable ex) {
            error = ex;
            throw ex instanceof RuntimeException ? (RuntimeException) ex : new RuntimeException(ex);
        } finally {
            exit(error);
        }
    }
}
