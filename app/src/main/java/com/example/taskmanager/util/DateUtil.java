package com.example.taskmanager.util;

import android.text.format.DateUtils;
import android.util.Log;

import com.example.taskmanager.receiver.NotificationReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DateUtil {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.ITALIAN);
    private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm", Locale.ITALIAN);
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.ITALIAN);

    public static SimpleDateFormat getFormatter(){
        return formatter;
    }

    public static SimpleDateFormat getDateTimeFormatter(){
        return dateTimeFormatter;
    }
    public static SimpleDateFormat getTimeFormatter(){
        return timeFormatter;
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

    public static Date getDateTimePlusDays(Date date, @SuppressWarnings("SameParameterValue") int i){
        Calendar c = getCalendar(date);
        c.add(Calendar.DATE, i);
        return c.getTime();
    }

    public static Date getDateTimePlusWeeks(Date date, int i){
        Calendar c = getCalendar(date);
        c.add(Calendar.WEEK_OF_YEAR, i);
        return c.getTime();
    }

    public static Date getDateTimePlusMonths(Date date, int i){
        Calendar c = getCalendar(date);
        c.add(Calendar.MONTH, i);
        return c.getTime();
    }

    public static Date getDateTimePlusYears(Date date, @SuppressWarnings("SameParameterValue") int i){
        Calendar c = getCalendar(date);
        c.add(Calendar.YEAR, i);
        return c.getTime();
    }

    public static Calendar getCalendar(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }

    public static long nowInMillis(){
        return Instant.now().atZone(ZoneId.of("Europe/Rome")).toEpochSecond()*1000;
    }

    public static ZonedDateTime fromMillisToZonedDateTime(long millis){
        Instant instant = Instant.ofEpochMilli(millis);
        return ZonedDateTime.ofInstant(instant, ZoneId.of("Europe/Rome"));
    }

    public static long fromStringDateTimeToMillis(String dateString){
        try {
            SimpleDateFormat dateFormat = getDateTimeFormatter();
            return Objects.requireNonNull(dateFormat.parse(dateString)).getTime();
        }catch(ParseException | NullPointerException | AssertionError e){
            Log.e(DateUtil.class.getName(), e.getMessage(), e);
            return 0;
        }
    }

    public static long fromStringDateToMillis(String dateString){
        try {
            SimpleDateFormat dateFormat = getFormatter();
            return Objects.requireNonNull(dateFormat.parse(dateString)).getTime();
        }catch(ParseException | NullPointerException | AssertionError e){
            Log.e(DateUtil.class.getName(), e.getMessage(), e);
            return 0;
        }
    }

    public static Date getDateTimeFromString(String stringDate) {
        try {
            return getDateTimeFormatter().parse(stringDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date getDateFromString(String stringDate){
        try {
            return getFormatter().parse(stringDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date getTodayWithoutTime(){
        return DateUtil.getCalendarWithoutTime(new Date()).getTime();
    }
}
