package financial.utils;

import java.time.LocalDate;
import java.time.YearMonth;

public class YearMonthUtils {
    private YearMonthUtils() {}
    public static LocalDate startOf(YearMonth ym) { return ym.atDay(1); }
    public static LocalDate startOfNext(YearMonth ym) { return ym.plusMonths(1).atDay(1); }
}
