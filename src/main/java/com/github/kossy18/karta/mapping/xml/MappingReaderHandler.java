/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.mapping.xml;

import com.github.kossy18.karta.mapping.EntityMappingProcessor;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class MappingReaderHandler extends DefaultHandler implements MappingHandler {

    private EntityMappingProcessor entityHelper;

    private MappingCallback mappingCallback;

    public MappingReaderHandler() {
        init();
    }

    @Override
    public void init() {
        entityHelper = new EntityMappingProcessor();
    }

    @Override
    public void addMappingCallback(MappingCallback callback) {
        this.mappingCallback = callback;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        switch (qName) {
            case "converter": {
                String name = attributes.getValue("name");
                String value = attributes.getValue("value");

                if (StringUtils.isEmpty(name)) {
                    name = value;
                }

                entityHelper.addConverter(name, value);
                break;
            }
            case "class": {
                entityHelper.setEntity(attributes.getValue("name"));
                break;
            }
            case "column": {
                entityHelper.addColumn(attributes.getValue("name"),
                        attributes.getValue("converter-ref"),
                        attributes.getValue("converter-data"));
                break;
            }
            case "property": {
                entityHelper.addProperty(attributes.getValue("name"),
                        attributes.getValue("column"),
                        attributes.getValue("order"),
                        attributes.getValue("converter-ref"),
                        attributes.getValue("converter-data"));
                break;
            }
            case "include": {
                mappingCallback.onMapResource(attributes.getValue("file"));
                break;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("class")) {
            mappingCallback.onMappingResult(entityHelper.buildEntity(), entityHelper.getConverterMap());
            entityHelper.clear();
        }
    }

    @Override
    public void complete() {
        if (entityHelper != null) {
            entityHelper.finish();
        }
    }
}
