/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.document;

import com.github.kossy18.karta.entity.Product;
import com.github.kossy18.karta.document.DocumentReaderFactory;
import com.github.kossy18.karta.document.ResourceToEntityProcessor;
import com.github.kossy18.karta.document.RowSeeker;
import com.github.kossy18.karta.AppConfig;
import com.github.kossy18.karta.ImporterConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= AppConfig.class)
public class ResourceToEntityProcessorTest {
    private static final String XML_FILE_PATH = "src/test/resources/test.xml";
    private static final String CSV_FILE_PATH = "src/test/resources/test.csv";
    private static final String XLSX_FILE_PATH = "src/test/resources/test.xlsx";

    @Autowired
    private ImporterConfig config;

    @Autowired
    private DocumentReaderFactory readerFactory;

    @Autowired
    private ResourceToEntityProcessor<Product> processor;

    @Before
    public void init() {
        config.build(XML_FILE_PATH);
    }

    @Test
    public void processAndGenerateEntityFromCsv() {
        RowSeeker seeker = readerFactory.getReader("csv").read(CSV_FILE_PATH, 0);
        List<Product> products = processor.process(seeker, Product.class);

        assertEquals(products.size(), 2);

        assertEquals(products.get(0).getId(), 1);
        assertEquals(products.get(0).getName(), "Book");
        assertEquals(products.get(0).getQuantity(), 5);
        assertEquals(products.get(0).getPrice(), 5.25f, 0.0f);
    }

    @Test
    public void processAndGenerateEntityFromXlsx() {
        RowSeeker seeker = readerFactory.getReader("spreadsheet").read(XLSX_FILE_PATH, 1);
        List<Product> products = processor.process(seeker, Product.class);

        assertEquals(products.size(), 2);

        assertEquals(products.get(1).getId(), 2);
        assertEquals(products.get(1).getName(), "Pen");
        assertEquals(products.get(1).getQuantity(), 3);
        assertEquals(products.get(1).getPrice(), 1.52f, 0.0f);
    }
}