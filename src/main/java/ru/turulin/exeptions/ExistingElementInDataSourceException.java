package ru.turulin.exeptions;

public class ExistingElementInDataSourceException extends Exception {
    public ExistingElementInDataSourceException(String message) {
        super(message);
    }
}
