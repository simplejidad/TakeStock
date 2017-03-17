package com.santiagogil.takestock.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateHelper {

    public static String dateFormat = "dd-MM-yyyy";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    public String getFormatedDayFromMiliseconds(Long miliseconds){

        Date date = new Date(miliseconds);

        return simpleDateFormat.format(date);
    }



}
