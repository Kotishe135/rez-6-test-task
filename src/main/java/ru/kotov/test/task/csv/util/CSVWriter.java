package ru.kotov.test.task.csv.util;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter implements Closeable {
    private final BufferedWriter writer;
    private final String delimiter;

    public CSVWriter(String fileName, String delimiter) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(fileName));
        this.delimiter = delimiter;
    }

    public void write(String[] data) throws IOException {
        for (int i = 0; i < data.length; i++) {
            writer.write(data[i]);
            if (i + 1 < data.length) {
                writer.write(delimiter);
            }
        }
        writer.write(System.lineSeparator());
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
