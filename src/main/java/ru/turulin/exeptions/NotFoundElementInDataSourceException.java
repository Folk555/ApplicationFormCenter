package ru.turulin.exeptions;

public class NotFoundElementInDataSourceException extends Exception {
    public NotFoundElementInDataSourceException(String message) {
        super(message);
    }
}
