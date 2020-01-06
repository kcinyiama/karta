/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.util;

import com.github.kossy18.karta.ImporterException;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;

public abstract class FileUtils {

    public static final int BUFFER_SIZE = 4096;

    // Does not deal with lines terminates by \r
    public static int countLines(String filePath) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(filePath))) {
            byte[] bf = new byte[BUFFER_SIZE];

            int numBytes = is.read(bf);
            if (numBytes == -1) {
                return 0;
            }

            int count = 1;
            while (numBytes != -1) {
                for (int i = 0; i < numBytes; i++) {
                    if (bf[i] == '\n') {
                        ++count;
                    }
                }
                numBytes = is.read(bf);
            }
            return count;
        }
    }

    public static File getFileResource(String fileName) {
        File f = new File(fileName);

        if (f.exists()) {
            return f;
        }

        URL resource = ClassLoader.getSystemClassLoader().getResource(fileName);
        if (resource == null) {
            throw new ImporterException("An error occurred while trying to retrieve file: " + fileName);
        }
        return new File(resource.getFile());
    }

    public static String getFileExtension(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return fileName;
        }
        int dot = fileName.lastIndexOf('.');
        int slash = fileName.lastIndexOf('/');

        return dot > slash ? fileName.substring(dot + 1) : fileName.substring(slash + 1);
    }
}
