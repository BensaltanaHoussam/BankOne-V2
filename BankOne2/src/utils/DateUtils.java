// src/utils/DateUtils.java
package utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    private DateUtils() {}

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static String formatDate(LocalDate date) {
        return date == null ? "-" : date.format(DATE_FMT);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "-" : dateTime.format(DATETIME_FMT);
    }

    public static LocalDate parseDate(String text) {
        return LocalDate.parse(text, DATE_FMT);
    }

    public static LocalDateTime parseDateTime(String text) {
        return LocalDateTime.parse(text, DATETIME_FMT);
    }
}
