package com.rutika.inventory.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtil {

    private static final DateTimeFormatter UTC_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);

    public static String toUtcString(Instant instant) {
        return UTC_FORMATTER.format(instant);
    }

    public static Instant toInstant(String utcString) {
        return LocalDateTime.parse(utcString, UTC_FORMATTER)
                .toInstant(ZoneOffset.UTC);
    }

    public static Instant nowUtc() {
        return Instant.now();
    }
}
