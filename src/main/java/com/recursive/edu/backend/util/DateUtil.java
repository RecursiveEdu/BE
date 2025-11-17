package com.recursive.edu.backend.util;

import java.util.Date;

/**
 * @author PrantikGuha
 * CreatedAt: {13-11-2025}
 */
public class DateUtil {
    public static long getMillis(long timeInMinutes) {
        return timeInMinutes * 60 * 1000;
    }
}
