package com.jnu.projectlab.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {

    // 날짜 포맷: YYYYMMDD
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 날짜시간 포맷: YYYY-MM-DD HH:mm:ss
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * YYYYMMDD 형식 문자열을 LocalDate로 변환
     * 예: "20250529" → LocalDate
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(dateString.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            // 파싱 실패 시 null 반환 (로그는 선택사항)
            return null;
        }
    }

    /**
     * YYYY-MM-DD HH:mm:ss 형식 문자열을 LocalDateTime으로 변환
     * 예: "2025-05-29 11:19:09" → LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(dateTimeString.trim(), DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            // 파싱 실패 시 null 반환 (로그는 선택사항)
            return null;
        }
    }
}