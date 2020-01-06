/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.document;

/**
 * A <tt>RowSeeker</tt> is used to transverse a document
 */
public interface RowSeeker {

    /**
     * Transverses to the next row of the document
     *
     * @return a <tt>Row</tt> which contains specific details of the transversed row
     */
    Row next();

    /**
     * Returns the total number of rows in the document
     *
     * @return the number of rows
     */
    int count();

    /**
     * Closes the connections used by the <tt>RowSeeker</tt>
     */
    void close();
}
