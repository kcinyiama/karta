/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.converters;

import com.github.kossy18.karta.document.Cell;

/**
 * Converts retrieved data from the supplied import document.
 * Defines a strategy for converting the data (of type <tt>String</tt>)
 */
public interface EntityConverter {

    /**
     * Converts a <tt>String</tt> to the target data type
     *
     * @param cell a cell containing the data to convert
     * @param extras extra argument, supplied from the mapping resource
     *
     * @return the converted value
     */
    Object convert(Cell cell, String extras);
}
