package ru.kotov.test.task.common.utils;

public enum AuxiliaryFiles {
    FIRST_AUXILIARY_FILE("firstAuxiliaryFile", "csv"),
    SECOND_AUXILIARY_FILE("secondAuxiliaryFile", "csv"),
    THIRD_AUXILIARY_FILE("thirdAuxiliaryFile", "csv");

    private final String prefix;
    private final String suffix;

    AuxiliaryFiles(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getAuxiliaryFileName() {
        return prefix + "." + suffix;
    }
}
