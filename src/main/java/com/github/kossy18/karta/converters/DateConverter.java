/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.converters;

import com.github.kossy18.karta.document.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Converts strings to a {@link java.util.Date Date} object
 *
 * @author Inyiama Kossy
 * @see EntityConverter
 */
public class DateConverter implements EntityConverter {
    private static final Logger log = LoggerFactory.getLogger(DateConverter.class);

    @Override
    public Object convert(Cell cell, String dateFormat) {
        try {
            log.debug("Converting string value {} to date with format {}", cell.getValue(), dateFormat);
            return new SimpleDateFormat(dateFormat).parse(cell.getValue());
        } catch (ParseException e) {
            throw new EntityConverterException("Could not parse date " + cell.getValue() + " with format " + dateFormat, e);
        }
    }
}
