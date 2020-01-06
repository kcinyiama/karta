/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.document;

import com.github.kossy18.karta.util.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpreadsheetDocumentReader extends AbstractDocumentReader {

    public SpreadsheetDocumentReader() {
        super(new Object());
    }

    @Override
    protected RowSeeker doRead(String filePath, int sheetNumber) throws IOException {
        return new SpreadsheetDocumentRowSeeker(filePath, sheetNumber);
    }

    private static class SpreadsheetDocumentRowSeeker extends AbstractRowSeeker {
        private InputStream in;

        private int linesCount = -1;

        private Cell[] cellHeaders;

        private Sheet sheet;
        private Iterator<org.apache.poi.ss.usermodel.Row> rowIterator;

        private SpreadsheetDocumentRowSeeker(String filePath, int sheetIndex) throws IOException {
            super(filePath);

            in = new FileInputStream(filePath);
            Workbook workbook = null;

            String extension = FileUtils.getFileExtension(filePath);
            if (extension.equals("xls")) {
                workbook = new HSSFWorkbook(in);
            }
            else if (extension.equals("xlsx")) {
                workbook = new XSSFWorkbook(in);
            }

            if (workbook != null) {
                int numOfSheets = workbook.getNumberOfSheets();

                if (sheetIndex < numOfSheets) {
                    sheet = workbook.getSheetAt(sheetIndex);
                    rowIterator = sheet.iterator();
                }
            }
        }

        @Override
        public Row next() {
            try {
                if (rowIterator.hasNext()) {
                    linesCount++;

                    org.apache.poi.ss.usermodel.Row sheetRow = rowIterator.next();

                    List<Cell> cellList;
                    if (linesCount == 0) {
                        cellList = retrieveCells(sheetRow, null);
                        cellHeaders = cellList.toArray(new Cell[0]);
                    }
                    else {
                        cellList = retrieveCells(sheetRow, new TriFunction<Cell, Integer, String, String>() {
                            @Override
                            public Cell apply(Integer index, String columnValue, String cellValue) {
                                return new DefaultCell(index, cellHeaders[index].getValue(), cellValue);
                            }
                        });
                    }
                    return new Row(linesCount, cellList);
                }
            }
            catch (DocumentReaderException e) {
                throw new DocumentReaderException("An error occurred while trying to read file", e);
            }
            return null;
        }

        @Override
        public int count() {
            return sheet != null ? sheet.getPhysicalNumberOfRows() : 0;
        }

        private List<Cell> retrieveCells(org.apache.poi.ss.usermodel.Row sheetRow, TriFunction<Cell, Integer, String, String> converter) {
            List<Cell> cellList = new ArrayList<>();

            Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = sheetRow.cellIterator();
            while (cellIterator.hasNext()) {
                org.apache.poi.ss.usermodel.Cell cell = cellIterator.next();
                String value = cell.toString();

                if (converter != null) {
                    cellList.add(converter.apply(cell.getColumnIndex(), value, value));
                }
                else {
                    cellList.add(new DefaultCell(cell.getColumnIndex(), value, value));
                }
            }
            return cellList;
        }

        @Override
        public void close() {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new DocumentReaderException("Error while closing input stream ", e);
                }
            }
        }
    }
}
