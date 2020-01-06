/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.mapping.xml;

import com.github.kossy18.karta.EntityDetails;
import com.github.kossy18.karta.converters.EntityConverter;
import org.xml.sax.ContentHandler;

import java.util.Map;

public interface MappingHandler extends ContentHandler {

    void init();

    void addMappingCallback(MappingCallback callback);

    void complete();

    interface MappingCallback {
        void onMapResource(String resource);
        void onMappingResult(EntityDetails entity, Map<String, EntityConverter> converterMap);
    }
}
