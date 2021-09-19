package ru.kotov.test.task;

import ru.kotov.test.task.common.utils.AuxiliaryFiles;
import ru.kotov.test.task.csv.utils.CSVReader;
import ru.kotov.test.task.csv.utils.CSVSorter;

import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        CSVSorter sorter = new CSVSorter();
        try(CSVReader reader =
                    new CSVReader(AuxiliaryFiles.THIRD_AUXILIARY_FILE.getAuxiliaryFileName(), ",")) {
            sorter.sort(args[0], (row1, row2) -> {
                int fieldCompareNumber = Integer.parseInt(args[1]);
                Long timestamp1 = Long.parseLong(row1[fieldCompareNumber]);
                Long timestamp2 = Long.parseLong(row2[fieldCompareNumber]);
                return -timestamp1.compareTo(timestamp2);
            });
            for (int i = 0; i < 10; i++) {
                String[] row = reader.next();
                System.out.println(Arrays.toString(Arrays.copyOf(row, row.length - 1)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
