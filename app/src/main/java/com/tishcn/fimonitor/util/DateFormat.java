package com.tishcn.fimonitor.util;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by leona on 7/4/2016.
 */
public class DateFormat {

    public static String formatDateTime(long millis){

        Locale locale = Locale.getDefault();
        java.text.DateFormat shortDatetime = java.text.DateFormat.getDateTimeInstance(
                java.text.DateFormat.SHORT, java.text.DateFormat.SHORT, locale);
        java.text.DateFormat shortTime = java.text.DateFormat.getTimeInstance(
                java.text.DateFormat.SHORT, locale);
        String datetime = shortDatetime.format(millis);

        if(isSameDay(millis)){
            datetime = shortTime.format(millis);
        }

        return datetime;
    }

    public static boolean isSameDay(long millis){
        Calendar today = Calendar.getInstance();
        today.getTimeInMillis();
        Calendar requestTime = Calendar.getInstance();
        requestTime.setTimeInMillis(millis);
        return today.get(Calendar.YEAR) == requestTime.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == requestTime.get(Calendar.DAY_OF_YEAR);
    }

    public static String durationFormat(long milliSeconds) {
        String duration = "";
        if(milliSeconds > 0){
            long weeks = TimeUnit.MILLISECONDS.toDays(milliSeconds) / 7;
            if(weeks > 0){
                milliSeconds = milliSeconds - (TimeUnit.DAYS.toMillis(7) * weeks);
                if(weeks > 1){
                    duration = duration.concat(", ").concat(String.valueOf(weeks)).concat(" Weeks");
                } else {
                    duration = duration.concat(", ").concat(String.valueOf(weeks)).concat(" Week");
                }
            }
            long days = TimeUnit.MILLISECONDS.toDays(milliSeconds);
            if(days > 0){
                milliSeconds = milliSeconds - TimeUnit.DAYS.toMillis(days);
                if(days > 1){
                    duration = duration.concat(", ").concat(String.valueOf(days)).concat(" Days");
                } else {
                    duration = duration.concat(", ").concat(String.valueOf(days)).concat(" Day");
                }
            }
            long hours = TimeUnit.MILLISECONDS.toHours(milliSeconds);
            if(hours > 0){
                milliSeconds = milliSeconds - TimeUnit.HOURS.toMillis(hours);
                if(hours > 1){
                    duration = duration.concat(", ").concat(String.valueOf(hours)).concat(" Hours");
                } else {
                    duration = duration.concat(", ").concat(String.valueOf(hours)).concat(" Hour");
                }
            }
            long minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds);
            if(minutes > 0){
                milliSeconds = milliSeconds - TimeUnit.MINUTES.toMillis(minutes);
                if(minutes > 1){
                    duration = duration.concat(", ").concat(String.valueOf(minutes)).concat(" Mins");
                } else {
                    duration = duration.concat(", ").concat(String.valueOf(minutes)).concat(" Min");
                }
            }
            long seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds);
            if(seconds > 0){
                if(seconds > 1){
                    duration = duration.concat(", ").concat(String.valueOf(seconds)).concat(" Secs");
                } else {
                    duration = duration.concat(", ").concat(String.valueOf(seconds)).concat(" Sec");
                }
            }
        } else {
            return Constants.NONE;
        }
        return duration.substring(2);
    }

}
