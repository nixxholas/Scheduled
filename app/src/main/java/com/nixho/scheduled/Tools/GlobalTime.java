package com.nixho.scheduled.Tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Local & Global Time Converter
 * http://stackoverflow.com/questions/37390080/convert-local-time-to-utc-and-vice-versa
 *
 * Created by nixho on 13-Oct-16.
 */

public class GlobalTime {
    public static Date currentLocalToUTC() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gmt = new Date(sdf.format(date));
        return gmt;
    }

    public static Date localToUTC(Date date) {
        String timeZone = Calendar.getInstance().getTimeZone().getID();
        Date utc = new Date(date.getTime() - TimeZone.getTimeZone(timeZone).getOffset(date.getTime()));
        return utc;
    }

    /**
     * Converting the global time to the current time
     * @param date
     * @return
     */
    public static Date utcToLocal(Date date) {
        String timeZone = Calendar.getInstance().getTimeZone().getID();
        Date local = new Date(date.getTime() + TimeZone.getTimeZone(timeZone).getOffset(date.getTime()));
        return local;
    }
}
