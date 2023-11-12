package ru.fusionsoft.dereferencer.core.exceptions;

public class DereferenceException extends Exception {
    public DereferenceException(String msg) {
        super(msg);
    }

    public DereferenceException(Throwable t) {
        super(t);
    }
}