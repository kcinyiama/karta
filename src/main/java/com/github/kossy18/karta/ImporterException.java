/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta;

public class ImporterException extends RuntimeException {

    public ImporterException(String message) {
        super(message);
    }

    public ImporterException(String message, Throwable cause) {
        super(message, cause);
    }
}
