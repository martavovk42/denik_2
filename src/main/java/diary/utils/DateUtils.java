package diary.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {

    private static final Locale CZ = new Locale("cs", "CZ");

    private static final DateTimeFormatter DAY_LONG =
            DateTimeFormatter.ofPattern("d. MMMM yyyy", CZ);

    private static final DateTimeFormatter TIME_SHORT =
            DateTimeFormatter.ofPattern("HH:mm", CZ);

    private static final String[] MESICE_CZ = {
            "Leden", "Únor", "Březen", "Duben", "Květen", "Červen",
            "Červenec", "Srpen", "Září", "Říjen", "Listopad", "Prosinec"
    };

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static LocalDateTime fromDate(LocalDate date) {
        return date == null ? LocalDateTime.now() : date.atStartOfDay();
    }

    public static boolean sameDay(LocalDateTime a, LocalDate b) {
        return a.toLocalDate().equals(b);
    }

    /** Naformátuje datum jako "11. května 2026". */
    public static String formatDay(LocalDate d) {
        return d.format(DAY_LONG);
    }

    public static String formatTime(LocalDateTime dt) {
        return dt.format(TIME_SHORT);
    }

    /** Český název měsíce s indexem 1..12 */
    public static String mesic(int monthValue) {
        return MESICE_CZ[monthValue - 1];
    }
}
