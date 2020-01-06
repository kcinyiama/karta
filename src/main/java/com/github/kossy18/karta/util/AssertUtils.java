/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.util;

public abstract class AssertUtils {

    public static void notEmpty(String s) {
        notNull(s);
        if (s.isEmpty()) {
            throw new IllegalArgumentException("String supplied must not be empty");
        }
    }

    public static void notNull(Object object) {
        notNull(object, "Argument supplied must not be null");
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
