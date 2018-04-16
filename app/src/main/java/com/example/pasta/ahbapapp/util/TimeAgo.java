package com.example.pasta.ahbapapp.util;

import android.text.format.DateUtils;
import android.util.Log;

import java.util.Date;

/**
 * Created by pasta on 16.04.2018.
 */

public class TimeAgo {

    public static String getTimeAgo(Date time){
        try {
            long timeMillis = time.getTime();
            long now = System.currentTimeMillis();
            return (String) DateUtils.getRelativeTimeSpanString
                    (timeMillis,now,DateUtils.MINUTE_IN_MILLIS);
        }catch (Exception e){
            Log.d("TimeAgo", "couldn't convert time");

        }
        return "";
    }
}
