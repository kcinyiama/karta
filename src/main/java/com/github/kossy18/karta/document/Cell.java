/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.document;

/**
 * Representation of a cell in a row in a document.<p>
 * The cell's value is of type String. To convert a cell's value
 * to a specific type the <tt>EntityConverter</tt> is used.
 */
public interface Cell {

    /**
     * Returns the index of the cell.
     *
     * @return the zero based index of the cell.
     */
    int getColumnIndex();

    /**
     * Returns the column name for the cell. The column name of a cell is the
     * cell's value at <tt>Row</tt> index 0.
     *
     * @return the column name of the cell.
     */
    String getColumnName();

    /**
     * Gets the value of the cell a string.
     *
     * @return the value of the cell as a stirng.
     */
    String getValue();
}
