package com.example.demo.util;

import java.util.Collection;

public class Utility {

    public static boolean isNullOrEmpty(Object obj) {
        if (obj instanceof String) {
            return ((String) obj).trim().isEmpty();
        }
        return obj == null;
    }

    public static boolean isNullOrEmpty(Collection<?> list) {
        return list == null || list.isEmpty();
    }

}
