package com.deyvitineo.notes.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    public static String getCurrentTimestamp(){

        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd,-yyyy h:mm a");
            String currentDateTime = dateFormat.format(new Date());

            return currentDateTime;
        } catch (Exception e){
            return null;
        }
    }
}
