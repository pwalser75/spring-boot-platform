package ch.frostnova.spring.boot.platform.core.logging.performance;

import java.math.BigDecimal;
import java.math.RoundingMode;

class InvocationInfo {

    private final static String SYMBOL_SPACE = " ";
    private final static String SYMBOL_INDENTATION = "+"; // unicode alternative: "\u2937"
    private final static String SYMBOL_RIGHT_ARROW = "->"; // unicode alternative: "\u2192"

    private final int level;
    private final String invocation;
    private int mergeCount = 1;
    private long startTimeNs;
    private long endTimeNs;
    private String result;
    private long nestedTimeNs;

    InvocationInfo(int level, String invocation, long startTimeNs) {
        this.level = level;
        this.startTimeNs = startTimeNs;
        this.invocation = invocation;
    }

    private static String formatTimeMs(long timeNs) {
        return BigDecimal.valueOf(timeNs * 0.000001).setScale(2, RoundingMode.HALF_EVEN) + " ms";
    }

    public int getLevel() {
        return level;
    }

    void done(long endTimeNs, String result) {
        this.endTimeNs = endTimeNs;
        this.result = result;
    }

    long getElapsedTimeNs() {
        return endTimeNs - startTimeNs;
    }

    void setNestedTimeNs(long timeNs) {
        nestedTimeNs = timeNs;
    }

    boolean merge(InvocationInfo other) {
        if (other != null && nestedTimeNs == 0 && other.nestedTimeNs == 0 && level == other.level && invocation.equals(other.invocation)) {
            startTimeNs = Math.min(startTimeNs, other.startTimeNs);
            endTimeNs = Math.max(endTimeNs, other.endTimeNs);
            mergeCount++;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {

        long durationNs = endTimeNs - startTimeNs;

        StringBuilder builder = new StringBuilder();
        if (level > 0) {
            builder.append("  ".repeat(level));
            builder.append(SYMBOL_INDENTATION);
            builder.append(SYMBOL_SPACE);
        }
        if (mergeCount > 1) {
            builder.append(mergeCount);
            builder.append("x ");
        }
        builder.append(invocation);
        builder.append(SYMBOL_SPACE);
        builder.append(SYMBOL_RIGHT_ARROW);
        builder.append(SYMBOL_SPACE);
        if (result != null) {
            builder.append(result);
            builder.append(", ");
        }
        builder.append(formatTimeMs(durationNs));
        if (nestedTimeNs > 0) {
            builder.append(", self: ");
            builder.append(formatTimeMs(durationNs - nestedTimeNs));
        }
        return builder.toString();
    }
}
