/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta;

import com.github.kossy18.karta.document.SpreadsheetDocumentReader;
import com.github.kossy18.karta.document.CsvDocumentReader;
import com.github.kossy18.karta.document.DocumentReaderFactory;
import com.github.kossy18.karta.document.ResourceToEntityProcessor;
import com.github.kossy18.karta.mapping.xml.MappingReader;
import com.github.kossy18.karta.mapping.xml.MappingReaderHandler;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {

    @Bean
    public ImporterConfig importerConfig() {
        return new ImporterConfig();
    }

    @Bean
    public ResourceToEntityProcessor resourceToEntityProcessor() {
        return new ResourceToEntityProcessor(importerConfig());
    }

    // --- DOCUMENT
    @Bean
    public ServiceLocatorFactoryBean documentReaderConfig() {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(DocumentReaderFactory.class);
        return factoryBean;
    }

    @Bean(name = "csv")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CsvDocumentReader csvDocumentReader() {
        return new CsvDocumentReader();
    }

    @Bean(name = "spreadsheet")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SpreadsheetDocumentReader spreadsheetDocumentReader() {
        return new SpreadsheetDocumentReader();
    }

    // --- MAPPING
    @Bean
    public MappingReader mappingReader() {
        return new MappingReader(mappingReaderHandler());
    }

    @Bean
    public MappingReaderHandler mappingReaderHandler() {
        return new MappingReaderHandler();
    }
}
