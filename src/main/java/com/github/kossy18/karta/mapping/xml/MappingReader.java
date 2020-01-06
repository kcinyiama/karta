/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.mapping.xml;

import com.github.kossy18.karta.EntityDetails;
import com.github.kossy18.karta.ImporterConfig;
import com.github.kossy18.karta.converters.EntityConverter;
import com.github.kossy18.karta.mapping.InvalidMappingException;
import com.github.kossy18.karta.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappingReader {
    private static final Logger log = LoggerFactory.getLogger(MappingReader.class);

    private MappingHandler handler;

    public MappingReader(MappingHandler handler) {
        this.handler = handler;
    }

    public void readMapping(String resource, Callback mappingResult) throws ParserConfigurationException, SAXException, IOException {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        final List<EntityDetails> documentEntities = new ArrayList<>();
        final Map<String, EntityConverter> converterMap = new HashMap<>();

        handler.addMappingCallback(new MappingHandler.MappingCallback() {
            @Override
            public void onMapResource(String resource) {
                log.info("Reading from resource: " + resource);

                try {
                    if (ImporterConfig.validate(resource)) {
                        SAXParser saxParser = factory.newSAXParser();
                        saxParser.parse(FileUtils.getFileResource(resource), (DefaultHandler) handler);
                    }
                } catch (SAXException | IOException | ParserConfigurationException e) {
                    throw new InvalidMappingException("An error occurred while trying to read mapping file: " + resource, e);
                }
            }

            @Override
            public void onMappingResult(EntityDetails entity, Map<String, EntityConverter> converter) {
                documentEntities.add(entity);
                converterMap.putAll(converter);
            }
        });

        saxParser.parse(FileUtils.getFileResource(resource), (DefaultHandler) handler);

        Map<Class, EntityDetails> entityMap = new HashMap<>();
        for (EntityDetails entity : documentEntities) {
            entityMap.put(entity.getEntityClass(), entity);
        }

        mappingResult.onCall(entityMap, converterMap);
    }

    public void complete() {
        handler.complete();
    }

    public interface Callback {
        void onCall(Map<Class, EntityDetails> entityMap, Map<String, EntityConverter> converterMap);
    }
}
