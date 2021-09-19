package ru.kotov.test.task.csv.util;

import ru.kotov.test.task.csv.util.exception.CSVInputException;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader implements Closeable {
    private final BufferedReader reader;
    private final String regexDelimiter;
    private final String[] headers;
    private String nextLine;

    public CSVReader(String fileName, String regexDelimiter) throws IOException {
        this.reader = new BufferedReader(new FileReader(fileName));
        this.regexDelimiter = regexDelimiter;
        headers = reader.readLine().split(regexDelimiter);
    }

    public String[] next() {
        String[] fields = new String[0];
        if (hasNext()) {
            fields = nextLine.split(regexDelimiter);
            nextLine = null;
        }
        return fields;
    }

    public boolean hasNext() {
        try {
            nextLine = nextLine == null ? reader.readLine() : nextLine;
        } catch (IOException e) {
            throw new CSVInputException("Input exception", e);
        }
        return nextLine != null && !nextLine.isEmpty();
    }

    public String[] getHeaders() {
        return headers;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
