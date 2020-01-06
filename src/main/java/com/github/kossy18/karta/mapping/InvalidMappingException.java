/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.mapping;

public class InvalidMappingException extends MappingException {

    public InvalidMappingException(String message) {
        super(message);
    }

    public InvalidMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
