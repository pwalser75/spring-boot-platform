package ch.frostnova.spring.boot.platform.core.scope;

import java.util.concurrent.Callable;

/**
 * Functional interface for a supplier which can throw a checked exception. (same as {@link Callable}).
 */
@FunctionalInterface
public interface CheckedSupplier<T> {

    /**
     * Functional contract
     *
     * @return return value
     * @throws Exception optional exception
     */
    T supply() throws Throwable;

    /**
     * Unchecked execution: execute checked and rethrow any exception as {@link RuntimeException}.
     *
     * @return T supplied value
     */
    default T supplyUnchecked() {
        try {
            return supply();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
