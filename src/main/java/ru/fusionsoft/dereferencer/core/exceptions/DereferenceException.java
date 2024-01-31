package ru.fusionsoft.dereferencer.core.exceptions;

public class DereferenceException extends Exception {
    public DereferenceException(String msg) {
        super(msg);
    }

    public DereferenceException(String msg, Throwable t) {
        super(msg, t);
    }
}
