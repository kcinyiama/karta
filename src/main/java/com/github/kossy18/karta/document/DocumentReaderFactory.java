/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.document;

public interface DocumentReaderFactory {

    DocumentReader getReader(String type);
}
