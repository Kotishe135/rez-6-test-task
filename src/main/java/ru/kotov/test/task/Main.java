package ru.kotov.test.task;

import ru.kotov.test.task.csv.util.CSVSorter;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        CSVSorter sorter = new CSVSorter();
        try {
            sorter.sort(args[0], (row1, row2) -> {
                int fieldCompareNumber = Integer.parseInt(args[1]);
                Long timestamp1 = Long.parseLong(row1[fieldCompareNumber]);
                Long timestamp2 = Long.parseLong(row2[fieldCompareNumber]);
                return -timestamp1.compareTo(timestamp2);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
