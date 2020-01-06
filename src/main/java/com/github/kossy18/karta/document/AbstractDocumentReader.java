/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.document;

import com.github.kossy18.karta.util.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class AbstractDocumentReader implements DocumentReader {

    private final Object lock;

    public AbstractDocumentReader(Object lock) {
        this.lock = lock;
    }

    @Override
    public RowSeeker read(String filePath, int sheetIndex) {
        synchronized (lock) {
            try {
                return doRead(filePath, sheetIndex);
            }
            catch (FileNotFoundException e) {
                throw new DocumentReaderException("Could not find the file " + filePath, e);
            }
            catch (IOException e) {
                throw new DocumentReaderException("An error occurred while reading file: " + filePath, e);
            }
        }
    }

    protected abstract RowSeeker doRead(String filePath, int sheetIndex) throws IOException;

    public abstract static class AbstractRowSeeker implements RowSeeker {
        private final String filePath;

        private int rowCount = 0;

        private boolean hasCountedRows = false;

        AbstractRowSeeker(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public abstract Row next();

        @Override
        public int count() {
            if (!hasCountedRows) {
                try {
                    rowCount = FileUtils.countLines(filePath);
                    hasCountedRows = true;
                } catch (IOException e) {
                    throw new DocumentReaderException("An error occurred while counting the number of rows", e);
                }
            }
            return rowCount;
        }

        @Override
        public abstract void close();

        public interface TriFunction<T, U, V, S> {
            T apply(U u, V v, S s);
        }
    }
}
