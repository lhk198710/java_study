package com.example.study.api.type;

import java.time.LocalDateTime;

public abstract class DateTimes {

    public static boolean isBetween(LocalDateTime now, LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            LocalDateTime temp = start;
            start = end;
            end = temp;
        }

        if (now.equals(start) || now.equals(end)) {
            return true;
        }

        return now.isAfter(start) && now.isBefore(end);
    }

    public static boolean isNotBetween(LocalDateTime now, LocalDateTime start, LocalDateTime end) {
        return !isBetween(now, start, end);
    }

    public static boolean nowIsBetween(LocalDateTime start, LocalDateTime end) {
        return isBetween(LocalDateTime.now(), start, end);
    }

    public static boolean nowIsNotBetween(LocalDateTime start, LocalDateTime end) {
        return !nowIsBetween(start, end);
    }
}
