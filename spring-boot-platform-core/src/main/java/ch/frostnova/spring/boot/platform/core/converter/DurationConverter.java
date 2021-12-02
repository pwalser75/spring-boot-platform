package ch.frostnova.spring.boot.platform.core.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.join;

@Component
public class DurationConverter implements Converter<String, Duration> {

    private final static BinaryOperator<String> TOKEN_PATTERN = (name, unit) -> String.format("(?:(?<%s>\\d+)\\s*%s)?", name, unit);

    private final static Pattern PATTERN = Pattern.compile("^\\s*([-])?" + join("\\s*",
            TOKEN_PATTERN.apply("weeks", "w"),
            TOKEN_PATTERN.apply("days", "d"),
            TOKEN_PATTERN.apply("hours", "h"),
            TOKEN_PATTERN.apply("minutes", "m"),
            TOKEN_PATTERN.apply("seconds", "s"),
            TOKEN_PATTERN.apply("milliseconds", "ms")
    ) + "\\s*$");

    @Override
    public Duration convert(String value) {
        if (value == null || value.isBlank() || value.trim().equals("-")) {
            return null;
        }
        Matcher matcher = PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("Illegal duration format '%s', expected '1w2d3h4m5s' or '1w 2d 3h 4m 5s' format (w=weeks,d=days,h=hours,m=minutes,s=seconds)", value));
        }

        boolean negative = "-".equals(matcher.group(1));
        int weeks = Optional.ofNullable(matcher.group("weeks")).map(Integer::parseInt).orElse(0);
        int days = Optional.ofNullable(matcher.group("days")).map(Integer::parseInt).orElse(0);
        int hours = Optional.ofNullable(matcher.group("hours")).map(Integer::parseInt).orElse(0);
        int minutes = Optional.ofNullable(matcher.group("minutes")).map(Integer::parseInt).orElse(0);
        int seconds = Optional.ofNullable(matcher.group("seconds")).map(Integer::parseInt).orElse(0);
        int milliseconds = Optional.ofNullable(matcher.group("milliseconds")).map(Integer::parseInt).orElse(0);
        Duration duration = Duration.ofDays(7L * weeks + days)
                .plus(Duration.ofHours(hours))
                .plus(Duration.ofMinutes(minutes))
                .plus(Duration.ofSeconds(seconds))
                .plus(Duration.ofMillis(milliseconds));
        return negative ? Duration.ofSeconds(0).minus(duration) : duration;
    }
}