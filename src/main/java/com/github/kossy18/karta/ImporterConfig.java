/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta;

import com.github.kossy18.karta.converters.EntityConverter;
import com.github.kossy18.karta.mapping.InvalidMappingException;
import com.github.kossy18.karta.mapping.MappingException;
import com.github.kossy18.karta.mapping.xml.MappingReader;
import com.github.kossy18.karta.util.AssertUtils;
import com.github.kossy18.karta.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.util.*;

/**
 * An instance of <tt>ImporterConfig</tt> allows the application
 * to specify mapping documents to be used when creating an
 * {@link EntityConverter} and an {@link EntityDetails}
 *
 * @author Inyiama kossy
 */
public class ImporterConfig {
    private static final Logger log = LoggerFactory.getLogger(ImporterConfig.class);

    private static final String SCHEMA_FILE_NAME = "karta-1.0.xsd";

    private static final String DEFAULT_MAPPING_FILE_NAME = "karta-mapping.xml";

    /**
     * A global list of entities used throughout the application where the key is the
     * class to be mapped to and the value contains the properties of the class to be mapped.
     */
    private Map<Class, EntityDetails> entityDetailsMap;

    /**
     * A global list of converters used throughout the application where the key is the
     * converter name or the FQN of the class if not supplied and the value is the
     * class that performs the actual conversion.
     */
    private Map<String, EntityConverter> entityConverterMap;

    private MappingReader mappingReader;

    public ImporterConfig() {
        entityDetailsMap = new HashMap<>();
        entityConverterMap = new HashMap<>();
    }

    @Autowired
    public void setMappingReader(MappingReader reader) {
        this.mappingReader = reader;
    }

    /**
     * Builds the <tt>ImporterConfig</tt> using the mapping named <tt>importer-mapping.xml</tt>
     * in the application's resources.
     *
     * @see #build(String)
     */
    public void build() {
        build(DEFAULT_MAPPING_FILE_NAME);
    }

    /**
     * Builds the <tt>ImporterConfig</tt> using the supplied resource.
     * The schema for the xml resource is defined in the document: <tt>karta-1.0.xsd</tt>
     *
     * @param resource the mapping resource
     */
    public void build(String resource) {
        log.info("Configuring from resource: " + resource);

        // TODO Detect stack overflow exc due to importing the same file
        // TODO Add warning when trying to map a column with few or more parameters
        try {
            AssertUtils.notEmpty(resource);
            if (validate(resource)) {
                log.info("Begin reading resource: " + resource);

                mappingReader.readMapping(resource, new MappingReader.Callback() {
                    @Override
                    public void onCall(Map<Class, EntityDetails> entityMap, Map<String, EntityConverter> converterMap) {
                        entityDetailsMap.putAll(entityMap);
                        entityConverterMap.putAll(converterMap);
                    }
                });
                mappingReader.complete();
                log.info("Completed reading resource: " + resource);
            }
        }
        catch (SAXException | ParserConfigurationException e) {
            throw new InvalidMappingException("Unable to read resource. Check if the resource is valid", e);
        }
        catch (IOException e) {
            throw new MappingException("An error occurred while reading the resource", e);
        }
    }

    public Map<String, EntityConverter> getEntityConverterMap() {
        return Collections.unmodifiableMap(entityConverterMap);
    }

    public Map<Class, EntityDetails> getEntityDetailsMap() {
        return Collections.unmodifiableMap(entityDetailsMap);
    }

    public static boolean validate(String resource) throws IOException, SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(FileUtils.getFileResource(SCHEMA_FILE_NAME));

        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(FileUtils.getFileResource(resource)));
        return true;
    }
}
