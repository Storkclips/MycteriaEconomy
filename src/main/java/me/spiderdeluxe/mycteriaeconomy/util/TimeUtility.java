package me.spiderdeluxe.mycteriaeconomy.util;

import lombok.experimental.UtilityClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@UtilityClass
public class TimeUtility {

    /**
     * Obtain time from string with date
     *
     * @param time the date
     */
    public Long convertTime(final String time) throws ParseException {
        final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        final Date date = format.parse(time);
        return date.getTime();
    }
}