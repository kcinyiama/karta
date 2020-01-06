/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.document;

import java.util.ArrayList;
import java.util.List;

/**
 * A representation of a row in a document.
 */
class Row {

    /**
     * The index of the row
     */
    private final int index;

    /**
     * The total cells in the row
     */
    private final List<Cell> cells;

    /**
     * Creates a new <tt>Row</tt>
     *
     * @param index the index of the row
     * @param cells the cells of the row
     */
    Row(int index, List<Cell> cells) {
        this.index = index;
        this.cells = cells;
    }

    /**
     * Returns the index of the row
     *
     * @return the zero based index of the row
     */
    int getIndex() {
        return index;
    }

    /**
     * Returns the cells belonging to the row
     *
     * @return the cells
     */
    List<Cell> getCells() {
        return new ArrayList<>(cells);
    }
}
