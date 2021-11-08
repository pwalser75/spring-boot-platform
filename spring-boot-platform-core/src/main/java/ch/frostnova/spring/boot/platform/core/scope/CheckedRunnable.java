package ch.frostnova.spring.boot.platform.core.scope;

/**
 * Functional interface for a runnable which can throw a checked exception.
 */
@FunctionalInterface
public interface CheckedRunnable {

    /**
     * Functional contract
     *
     * @throws Exception optional exception
     */
    void run() throws Throwable;
    
    /**
     * Unchecked execution: execute checked and rethrow any exception as {@link RuntimeException}.
     */
    default void runUnchecked() {
        try {
            run();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
