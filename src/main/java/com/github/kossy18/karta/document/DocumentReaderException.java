/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.document;

import com.github.kossy18.karta.ImporterException;

public class DocumentReaderException extends ImporterException {

    public DocumentReaderException(String message) {
        super(message);
    }

    public DocumentReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
