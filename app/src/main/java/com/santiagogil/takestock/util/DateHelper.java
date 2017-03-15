package com.santiagogil.takestock.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateHelper {

    public static String dateFormat = "dd-MM-yyyy";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    public String ConvertIntegerToFormattedDate(Integer dateInInteger){

        Date date = new Date(integerToMiliseconds(dateInInteger));

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);

        return simpleDateFormat.format(gregorianCalendar.getTime());
    }

    public Long integerToMiliseconds(Integer dateInInteger){

        return Long.valueOf(dateInInteger * 1000*60*60*24);
    }

    public Integer currentDateInIntegerFromMilliseconds(){

        return (Integer) (int) (System.currentTimeMillis()/1000/60/60/24);
    }

}
