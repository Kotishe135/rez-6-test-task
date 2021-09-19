package ru.kotov.test.task.common.utils;

import ru.kotov.test.task.csv.utils.CSVReader;
import ru.kotov.test.task.csv.utils.CSVWriter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class AuxiliaryFileCreator {

    public static void createAuxiliaryFiles(String fileName) throws IOException {
        File fistAuxiliaryFile = File.createTempFile(
                AuxiliaryFiles.FIRST_AUXILIARY_FILE.getPrefix(), AuxiliaryFiles.FIRST_AUXILIARY_FILE.getSuffix());
        File secondAuxiliaryFile = File.createTempFile(
                AuxiliaryFiles.SECOND_AUXILIARY_FILE.getPrefix(), AuxiliaryFiles.SECOND_AUXILIARY_FILE.getSuffix());
        File thirdAuxiliaryFile = File.createTempFile(
                AuxiliaryFiles.THIRD_AUXILIARY_FILE.getPrefix(), AuxiliaryFiles.THIRD_AUXILIARY_FILE.getSuffix());
        fistAuxiliaryFile.deleteOnExit();
        secondAuxiliaryFile.deleteOnExit();
        thirdAuxiliaryFile.deleteOnExit();
        try (CSVReader reader = new CSVReader(fileName, ",");
             CSVWriter writer = new CSVWriter(AuxiliaryFiles.THIRD_AUXILIARY_FILE.getAuxiliaryFileName(), ",")) {
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
}
