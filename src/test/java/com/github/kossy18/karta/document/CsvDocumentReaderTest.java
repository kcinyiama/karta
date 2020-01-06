/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.document;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CsvDocumentReaderTest {
    private static final String CSV_FILE_PATH = "src/test/resources/test.csv";

    @Test
    public void readAndVerifyCsvDocument() {
        DocumentReader reader = new CsvDocumentReader();

        RowSeeker seeker = reader.read(CSV_FILE_PATH, -1);
        seeker.next(); // Skip the header row
        List<Cell> cells = seeker.next().getCells();
        seeker.close();

        assertEquals(cells.get(0).getValue(), "1");
        assertEquals(cells.get(1).getValue(), "Book");
        assertEquals(cells.get(2).getValue(), "5");
        assertEquals(cells.get(3).getValue(), "5.25");
    }

    @Test
    public void readAndCountCsvDocument() {
        DocumentReader reader = new CsvDocumentReader();

        RowSeeker seeker = reader.read(CSV_FILE_PATH, -1);
        assertEquals(seeker.count(), 3);
    }
}