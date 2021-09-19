package ru.kotov.test.task.csv.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class CSVSorter {
    private static final String PREFIX_OF_FIRST_AUXILIARY_FILE = "firstAuxiliaryFile";
    private static final String PREFIX_OF_SECOND_AUXILIARY_FILE = "secondAuxiliaryFile";
    private static final String PREFIX_OF_THIRD_AUXILIARY_FILE = "thirdAuxiliaryFile";
    private static final String SUFFIX_OF_AUXILIARY_FILES = "csv";

    public void sort(String fileName, Comparator<String[]> comparator) throws IOException {
        File fistAuxiliaryFile = File.createTempFile(PREFIX_OF_FIRST_AUXILIARY_FILE, SUFFIX_OF_AUXILIARY_FILES);
        File secondAuxiliaryFile = File.createTempFile(PREFIX_OF_SECOND_AUXILIARY_FILE, SUFFIX_OF_AUXILIARY_FILES);
        File thirdAuxiliaryFile = File.createTempFile(PREFIX_OF_SECOND_AUXILIARY_FILE, SUFFIX_OF_AUXILIARY_FILES);
        addRowNumbers(fileName);
        boolean isNeedRepeat = true;
        while (isNeedRepeat) {
            splitIntoSeries(comparator);
            isNeedRepeat = merge(comparator);
        }
        fistAuxiliaryFile.deleteOnExit();
        secondAuxiliaryFile.deleteOnExit();
        thirdAuxiliaryFile.deleteOnExit();
    }

    private String createAuxiliaryFileName(String prefix) {
        return prefix + "." + SUFFIX_OF_AUXILIARY_FILES;
    }

    private void addRowNumbers(String fileName) throws IOException {
        try (CSVReader reader = new CSVReader(fileName, ",");
             CSVWriter writer = new CSVWriter(createAuxiliaryFileName(PREFIX_OF_THIRD_AUXILIARY_FILE), ",")) {
            String[] headers = reader.getHeaders();
            headers = Arrays.copyOf(headers, headers.length + 1);
            headers[headers.length - 1] = "rowNumber";
            writer.write(headers);
            Long i = 0L;
            while (reader.hasNext()) {
                String[] row = reader.next();
                row = Arrays.copyOf(row, row.length + 1);
                row[row.length - 1] = (i++).toString();
                writer.write(row);
            }
        }
    }

    private Comparator<String[]> getExtendedComparator(Comparator<String[]> comparator) {
        return (row1, row2) -> {
            int compareResult = comparator.compare(row1, row2);
            if (compareResult != 0) {
                return compareResult;
            }
            Long timestamp1 = Long.parseLong(row1[row1.length - 1]);
            Long timestamp2 = Long.parseLong(row2[row1.length - 1]);
            return -timestamp1.compareTo(timestamp2);
        };
    }

    private void splitIntoSeries(Comparator<String[]> comparator) throws IOException {
        try (CSVReader reader = new CSVReader(createAuxiliaryFileName(PREFIX_OF_THIRD_AUXILIARY_FILE), ",");
             CSVWriter writerFirstAuxiliary =
                     new CSVWriter(createAuxiliaryFileName(PREFIX_OF_FIRST_AUXILIARY_FILE), ",");
             CSVWriter writerSecondAuxiliary =
                     new CSVWriter(createAuxiliaryFileName(PREFIX_OF_SECOND_AUXILIARY_FILE), ",")) {
            if (!reader.hasNext()) {
                return;
            }
            writerFirstAuxiliary.write(reader.getHeaders());
            writerSecondAuxiliary.write(reader.getHeaders());
            CSVWriter currentWriter = writerFirstAuxiliary;
            String[] prevRow = reader.next();
            currentWriter.write(prevRow);
            while (reader.hasNext()) {
                String[] currentRow = reader.next();
                if (getExtendedComparator(comparator).compare(currentRow, prevRow) < 0) {
                    currentWriter = currentWriter == writerFirstAuxiliary
                            ? writerSecondAuxiliary : writerFirstAuxiliary;
                }
                currentWriter.write(currentRow);
                prevRow = currentRow;
            }
        }
    }

    private boolean merge(Comparator<String[]> comparator) throws IOException {
        boolean isNeedRepeatLoop = false;
        try (CSVWriter writer = new CSVWriter(createAuxiliaryFileName(PREFIX_OF_THIRD_AUXILIARY_FILE), ",");
             CSVReader readerFirstAuxiliary =
                     new CSVReader(createAuxiliaryFileName(PREFIX_OF_FIRST_AUXILIARY_FILE), ",");
             CSVReader readerSecondAuxiliary =
                     new CSVReader(createAuxiliaryFileName(PREFIX_OF_SECOND_AUXILIARY_FILE), ",")) {
            CSVReader currentReader = readerFirstAuxiliary;
            writer.write(currentReader.getHeaders());
            String[] minRow = readerSecondAuxiliary.next();
            while (readerFirstAuxiliary.hasNext() && readerSecondAuxiliary.hasNext()) {
                String[] currentRow = currentReader.next();
                if (getExtendedComparator(comparator).compare(minRow, currentRow) < 0) {
                    writer.write(minRow);
                    minRow = currentRow;
                    currentReader = currentReader == readerFirstAuxiliary
                            ? readerSecondAuxiliary : readerFirstAuxiliary;
                    isNeedRepeatLoop = true;
                } else {
                    writer.write(currentRow);
                }
            }
            if (minRow.length > 0) {
                writer.write(minRow);
            }
            while (readerFirstAuxiliary.hasNext()) {
                writer.write(readerFirstAuxiliary.next());
            }
            while (readerSecondAuxiliary.hasNext()) {
                writer.write(readerSecondAuxiliary.next());
            }
        }
        return isNeedRepeatLoop;
    }
}
