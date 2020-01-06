/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.document;

/**
 * Responsible for creating a <tt>RowSeeker</tt> which
 * has a reference to the import document.
 *
 * @see RowSeeker
 */
public interface DocumentReader {

    /**
     * Opens a connection to the actual file specified by the file path
     * which in turn creates a <tt>RowSeeker</tt> which is used to transverse
     * the file line by line.
     *
     * @param filePath the path to the import document
     * @param sheetIndex the index of the sheet (If the import document is a spreadsheet document)
     * @return a <tt>RowSeeker</tt> for transversing the file.
     */
    RowSeeker read(String filePath, int sheetIndex);
}
