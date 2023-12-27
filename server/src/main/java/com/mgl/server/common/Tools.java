package com.mgl.server.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools
{
    public static void cout(Object object){
        System.out.println("~~~~~~~~~~~~~~~~");
        System.out.println(object);
        System.out.println("~~~~~~~~~~~~~~~~");
    }

    public static String dateFormat(String dateTime)
    {
        dateTime = dateTime.replace("Z", " UTC");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            Date time = format.parse(dateTime);
            String result = defaultFormat.format(time);
            return result;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static String timeFormat(String dateTime)
    {
        dateTime = dateTime.replace("Z", " UTC");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        SimpleDateFormat defaultFormat = new SimpleDateFormat("HH:mm:ss");
        try
        {
            Date time = format.parse(dateTime);
            String result = defaultFormat.format(time);
            return result;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
