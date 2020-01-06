/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

public final class Property {
    private static final Logger LOG = LoggerFactory.getLogger(Property.class);

    private final int order;

    private final String name;

    private Class<?>[] parameterTypes;

    private Map<Pattern, Converter> patternConverterMap;

    public Property(String name, Map<String, Converter> columnPatternConverterRef, int order) {
        this.name = name;
        this.order = order;
        setColumnPattern(columnPatternConverterRef);
    }

    public int getOrder() {
        return order;
    }

    public String getName() {
        return name;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Map<Pattern, Converter> getPatternConverterMap() {
        return patternConverterMap;
    }

    public void setColumnPattern(Map<String, Converter> columnPatterns) {
        if (patternConverterMap == null) {
            patternConverterMap = new LinkedHashMap<>(columnPatterns.size());
        }
        for (Map.Entry<String, Converter> entry : columnPatterns.entrySet()) {
            if (!StringUtils.isEmpty(entry.getKey())) {
                patternConverterMap.put(Pattern.compile(entry.getKey(), Pattern.CASE_INSENSITIVE), entry.getValue());
            }
        }
    }

    public int getColumnSize() {
        return patternConverterMap.size();
    }

    @Override
    public String toString() {
        return "Property{" +
                "order=" + order +
                ", name='" + name + '\'' +
                ", patternConverterMap=" + patternConverterMap +
                '}';
    }

    public static class Converter {
        private String ref;
        private String data;

        public Converter(String ref, String data) {
            this.ref = ref;
            this.data = data;
        }

        public String getRef() {
            return ref;
        }

        public String getData() {
            return data;
        }

        @Override
        public String toString() {
            return "Converter{" +
                    "ref='" + ref + '\'' +
                    ", data='" + data + '\'' +
                    '}';
        }
    }
}
