package edu.neu.madcourse.gowalk.db;

import androidx.room.TypeConverter;

import java.sql.Date;

public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
       return value == null ? null : new Date(value*1000);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        //milliseconds timestamp will be converted to seconds timestamp to match the strftime for query
        return date == null ? null : date.getTime()/1000;
    }
}
