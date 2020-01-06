/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.converters;

import com.github.kossy18.karta.document.Cell;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberConverter implements EntityConverter {
    private static final Logger log = LoggerFactory.getLogger(NumberConverter.class);

    @Override
    public Object convert(Cell cell, String numberType) {
        log.debug("Converting string value '{}' to Number", cell.getValue());

        if (StringUtils.isEmpty(numberType)) {
            return 0;
        }

        try {
            NumberType type = NumberType.valueOf(numberType.toUpperCase());

            switch (type) {
                case INTEGER:
                    return Double.valueOf(cell.getValue()).intValue();
                case FLOAT:
                    return Double.valueOf(cell.getValue()).floatValue();
                case DOUBLE:
                    return Double.valueOf(cell.getValue());
                case LONG:
                    return Double.valueOf(cell.getValue()).longValue();
                case BYTE:
                    return Double.valueOf(cell.getValue()).byteValue();
                case SHORT:
                    return Double.valueOf(cell.getValue()).shortValue();
            }
        } catch (NumberFormatException e) {
            throw new EntityConverterException("Could not convert '" + cell.getValue() + "' to the type: " + numberType, e);
        } catch (IllegalArgumentException e) {
            throw new EntityConverterException("Could not convert to '" + numberType + "'", e);
        }
        return 0;
    }

    public enum NumberType {
        INTEGER, FLOAT, DOUBLE, LONG, BYTE, SHORT;
    }
}
