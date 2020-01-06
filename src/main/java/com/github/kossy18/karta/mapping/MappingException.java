/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.mapping;

import com.github.kossy18.karta.ImporterException;

public class MappingException extends ImporterException {
    public MappingException(String message) {
        super(message);
    }

    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
