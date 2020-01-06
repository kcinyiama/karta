/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.converters;

import com.github.kossy18.karta.ImporterException;

/**
 * Indicates that an entity conversion could not be finished
 */
public class EntityConverterException extends ImporterException {

    public EntityConverterException(String message, Throwable cause) {
        super(message, cause);
    }
}
