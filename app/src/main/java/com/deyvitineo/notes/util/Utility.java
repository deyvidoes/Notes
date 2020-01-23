package com.deyvitineo.notes.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM-dd,-yyyy h:mm a");

    public static String getCurrentTimestamp(){

        try{
            String currentDateTime = DATE_FORMAT.format(new Date());

            return currentDateTime;
        } catch (Exception e){
            return null;
        }
    }

    public static boolean canNoteBeSaved(String title, String content){
        return (title.trim().isEmpty() || content.trim().isEmpty() ? false: true);
    }
}
