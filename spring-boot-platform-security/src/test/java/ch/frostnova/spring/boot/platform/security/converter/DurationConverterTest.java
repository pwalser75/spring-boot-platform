package ch.frostnova.spring.boot.platform.security.converter;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.time.Duration.parse;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DurationConverterTest {

    private final DurationConverter durationConverter = new DurationConverter();

    @Test
    public void testEmpty() {
        assertThat(durationConverter.convert(null)).isNull();
    }

    @Test
    public void testTrivial() {
        assertThat(durationConverter.convert("")).isNull();
    }

    @Test
    public void testConversionSuccessful() {

        assertThat(durationConverter.convert("123d")).isEqualTo(parse("P123D"));
        assertThat(durationConverter.convert("-123d")).isEqualTo(parse("P-123D"));

        assertThat(durationConverter.convert("12h34m56s")).isEqualTo(parse("PT12H34M56S"));
        assertThat(durationConverter.convert("-12h34m56s")).isEqualTo(parse("PT-12H-34M-56S"));

        assertThat(durationConverter.convert("1d2h3m4s500ms")).isEqualTo(parse("P1DT2H3M4.5S"));
        assertThat(durationConverter.convert("-1d2h3m4s500ms")).isEqualTo(parse("P-1DT-2H-3M-4.5S"));

        assertThat(durationConverter.convert("1w2d3h4m5s")).isEqualTo(parse("PT219H4M5S"));
        assertThat(durationConverter.convert(" 1w 2d 3h 4m 5s")).isEqualTo(parse("PT219H4M5S"));
        assertThat(durationConverter.convert(" 1 w 2 d 3 h 4 m 5 s ")).isEqualTo(parse("PT219H4M5S"));

        assertThat(durationConverter.convert("1d2h3m4s")).isEqualTo(parse("PT26H3M4S"));
        assertThat(durationConverter.convert("1d 2h 3m 4s")).isEqualTo(parse("PT26H3M4S"));

        assertThat(durationConverter.convert("11d22h33m44s")).isEqualTo(parse("PT286H33M44S"));
        assertThat(durationConverter.convert("11d 22h 33m 44s")).isEqualTo(parse("PT286H33M44S"));

        assertThat(durationConverter.convert("111w")).isEqualTo(parse("PT18648H"));
        assertThat(durationConverter.convert("222d")).isEqualTo(parse("PT5328H"));
        assertThat(durationConverter.convert("333h")).isEqualTo(parse("PT333H"));
        assertThat(durationConverter.convert("444m")).isEqualTo(parse("PT7H24M"));
        assertThat(durationConverter.convert("555s")).isEqualTo(parse("PT9M15S"));

        assertThat(durationConverter.convert("3d 5h")).isEqualTo(parse("PT77H"));
        assertThat(durationConverter.convert("2h 15m")).isEqualTo(parse("PT2H15M"));
        assertThat(durationConverter.convert("5m20s")).isEqualTo(parse("PT5M20S"));

        assertThat(durationConverter.convert("500m2000s")).isEqualTo(parse("PT8H53M20S"));

    }

    @Test
    public void testIllegalFormat() {
        assertThatThrownBy(() -> durationConverter.convert("2h 3d")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> durationConverter.convert("2h -5m")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> durationConverter.convert("3.5h")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> durationConverter.convert("xxx")).isInstanceOf(IllegalArgumentException.class);
    }

    @RepeatedTest(100)
    public void testConversionSuccessfulMonteCarlo() {

        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        Supplier<Integer> randomOrEmpty = () -> rnd.nextBoolean() ? rnd.nextInt(0, 100) : null;

        boolean negative = rnd.nextBoolean();
        Integer w = randomOrEmpty.get();
        Integer d = randomOrEmpty.get();
        Integer h = randomOrEmpty.get();
        Integer m = randomOrEmpty.get();
        Integer s = randomOrEmpty.get();
        Integer ms = randomOrEmpty.get();

        Duration expected = Duration.ZERO
                .plus(numberOrZero(w) * 7, DAYS)
                .plus(numberOrZero(d), DAYS)
                .plus(numberOrZero(h), HOURS)
                .plus(numberOrZero(m), MINUTES)
                .plus(numberOrZero(s), SECONDS)
                .plus(numberOrZero(ms), MILLIS);
        if (w == null && d == null && h == null && m == null && s == null && ms == null) {
            expected = null;
        } else if (negative) {
            expected = Duration.ZERO.minus(expected);
        }

        String text = (negative ? "-" : "") +
                Stream.of(suffixed(w, "w"),
                                suffixed(d, "d"),
                                suffixed(h, "h"),
                                suffixed(m, "m"),
                                suffixed(s, "s"),
                                suffixed(ms, "ms"))
                        .filter(Objects::nonNull)
                        .collect(joining());

        assertThat(durationConverter.convert(text)).describedAs(text).isEqualTo(expected);
    }

    private int numberOrZero(Integer n) {
        return Optional.ofNullable(n).orElse(0);
    }

    private String suffixed(Integer n, String suffix) {
        return n != null ? n + suffix : "";
    }
}
