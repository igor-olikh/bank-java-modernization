package com.bank.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Centralised date/time utilities.
 * All timestamps in the system use the Asia/Jerusalem timezone.
 */
public final class DateUtil {

    /** Israel Standard Time / Israel Daylight Time (UTC+2 / UTC+3). */
    public static final ZoneId ISRAEL_ZONE = ZoneId.of("Asia/Jerusalem");

    /** Full timestamp: 25-03-2026 14:55:34 */
    public static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    /** Date only: 25-03-2026 */
    public static final DateTimeFormatter D_FMT  = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private DateUtil() {}

    /** Current date-time in Israel timezone. Use instead of LocalDateTime.now(). */
    public static LocalDateTime now() {
        return LocalDateTime.now(ISRAEL_ZONE);
    }

    /** Format a LocalDateTime as "dd-MM-yyyy HH:mm:ss". */
    public static String formatDateTime(LocalDateTime dt) {
        return dt == null ? "" : dt.format(DT_FMT);
    }

    /** Format a LocalDate as "dd-MM-yyyy". */
    public static String formatDate(LocalDate d) {
        return d == null ? "" : d.format(D_FMT);
    }

    /** Format a LocalDateTime showing date part only as "dd-MM-yyyy". */
    public static String formatDateOnly(LocalDateTime dt) {
        return dt == null ? "" : dt.toLocalDate().format(D_FMT);
    }
}
