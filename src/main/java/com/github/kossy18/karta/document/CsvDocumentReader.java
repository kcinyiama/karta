/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.document;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.*;
import java.util.*;

/**
 * An implementation of <tt>DocumentReader</tt> which reads CSV files
 */
public class CsvDocumentReader extends AbstractDocumentReader {

    private char separator = ',';
    private boolean ignoreQuotations = false;

    public CsvDocumentReader() {
        super(new Object());
    }

    public CsvDocumentReader setSeparator(char separator) {
        this.separator = separator;
        return this;
    }

    public CsvDocumentReader setIgnoreQuotations(boolean ignoreQuotations) {
        this.ignoreQuotations = ignoreQuotations;
        return this;
    }

    @Override
    protected RowSeeker doRead(String filePath, int sheetIndex) throws FileNotFoundException {
        return new CsvDocumentRowSeeker(filePath, separator, ignoreQuotations);
    }

    private static class CsvDocumentRowSeeker extends AbstractRowSeeker {
        private int linesCount = -1;

        private CSVReader csvReader;

        private Cell[] cellHeaders;

        private CsvDocumentRowSeeker(String filePath, char separator, boolean ignoreQuotations) throws FileNotFoundException {
            super(filePath);

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(separator)
                    .withIgnoreQuotations(ignoreQuotations)
                    .build();

            csvReader = new CSVReaderBuilder(br)
                    .withCSVParser(parser)
                    .build();
        }

        @Override
        public Row next() {
            try {
                String[] data = csvReader.readNext();

                if (data != null) {
                    linesCount++;

                    Cell[] cells = new Cell[data.length];

                    if (linesCount == 0) {
                        for (int i = 0; i < data.length; i++) {
                            String s = data[i].trim();
                            cells[i] = new DefaultCell(i, s, s);
                        }
                        cellHeaders = cells;
                    }
                    else {
                        for (int i = 0; i < data.length; i++) {
                            String s = data[i].trim();
                            cells[i] = new DefaultCell(i, cellHeaders[i].getValue(), s);
                        }
                    }

                    return new Row(linesCount, new ArrayList<>(Arrays.asList(cells)));
                }
            } catch (IOException e) {
                throw new DocumentReaderException("An error occurred while trying to read file", e);
            }
            return null;
        }

        @Override
        public void close() {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (IOException e) {
                    throw new DocumentReaderException("Error while closing csv reader", e);
                }
            }
        }
    }
}
