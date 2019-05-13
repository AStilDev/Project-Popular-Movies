package com.example.popularmovies;

import android.arch.persistence.room.TypeConverter;

import java.util.Arrays;

public class ArrayConverter {

    @TypeConverter
    public static int[] fromString(String value) {
        if (!value.equals("null") && !value.equals("[]")) {
            String[] items = value.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
            int[] arrayInt = new int[items.length];
            for (int i = 0; i < items.length; i++) {
                arrayInt[i] = Integer.parseInt(items[i]);
            }
            return arrayInt;
        }

        return null;
    }

    @TypeConverter
    public static String fromIntArray(int[] list) {
        String arrayStr = Arrays.toString(list);
        return arrayStr;
    }
}
