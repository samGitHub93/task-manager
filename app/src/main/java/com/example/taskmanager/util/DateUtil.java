package com.example.taskmanager.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class DateUtil {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);

    public static SimpleDateFormat getFormatter(){
        return formatter;
    }

    public static List<Date> getDatesBetween(Date startDate, Date endDate) {
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = getCalendarWithoutTime(startDate);
        Calendar endCalendar = getCalendarWithoutTime(endDate);
        while (calendar.before(endCalendar) || calendar.equals(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }

    public static boolean isDateInRange(List<Date> dates, Date date) {
        try {
            for (Date dt : dates) {
                dt = formatter.parse(formatter.format(dt));
                if (date.compareTo(dt) == 0) return true;
            }
        }catch (ParseException pe){
            pe.getStackTrace();
        }
        return false;
    }

    public static Date getDatePlusDays(Date date, @SuppressWarnings("SameParameterValue") int i){
        Calendar c = getCalendarWithoutTime(date);
        c.add(Calendar.DATE, i);
        return c.getTime();
    }

    public static Date getDatePlusWeeks(Date date, int i){
        Calendar c = getCalendarWithoutTime(date);
        c.add(Calendar.WEEK_OF_YEAR, i);
        return c.getTime();
    }

    public static Date getDatePlusMonths(Date date, int i){
        Calendar c = getCalendarWithoutTime(date);
        c.add(Calendar.MONTH, i);
        return c.getTime();
    }

    public static Date getDatePlusYears(Date date, @SuppressWarnings("SameParameterValue") int i){
        Calendar c = getCalendarWithoutTime(date);
        c.add(Calendar.YEAR, i);
        return c.getTime();
    }

    public static Calendar getCalendarWithoutTime(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
