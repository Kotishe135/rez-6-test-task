package ru.kotov.test.task.csv.utils;

import ru.kotov.test.task.common.utils.AuxiliaryFileCreator;
import ru.kotov.test.task.common.utils.AuxiliaryFiles;

import java.io.IOException;
import java.util.Comparator;

public class CSVSorter {

    public void sort(String fileName, Comparator<String[]> comparator) throws IOException {
        AuxiliaryFileCreator.createAuxiliaryFiles(fileName);
        boolean isNeedRepeat = true;
        while (isNeedRepeat) {
            splitIntoSeries(comparator);
            isNeedRepeat = merge(comparator);
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
        try (CSVReader reader = new CSVReader(AuxiliaryFiles.THIRD_AUXILIARY_FILE.getAuxiliaryFileName(), ",");
             CSVWriter writerFirstAuxiliary =
                     new CSVWriter(AuxiliaryFiles.FIRST_AUXILIARY_FILE.getAuxiliaryFileName(), ",");
             CSVWriter writerSecondAuxiliary =
                     new CSVWriter(AuxiliaryFiles.SECOND_AUXILIARY_FILE.getAuxiliaryFileName(), ",")) {
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
        try (CSVWriter writer = new CSVWriter(AuxiliaryFiles.THIRD_AUXILIARY_FILE.getAuxiliaryFileName(), ",");
             CSVReader readerFirstAuxiliary =
                     new CSVReader(AuxiliaryFiles.FIRST_AUXILIARY_FILE.getAuxiliaryFileName(), ",");
             CSVReader readerSecondAuxiliary =
                     new CSVReader(AuxiliaryFiles.SECOND_AUXILIARY_FILE.getAuxiliaryFileName(), ",")) {
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
