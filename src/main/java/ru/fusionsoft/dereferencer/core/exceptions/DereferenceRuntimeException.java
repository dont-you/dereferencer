package ru.fusionsoft.dereferencer.core.exceptions;

public class DereferenceRuntimeException extends RuntimeException {
    public DereferenceRuntimeException(String msg) {
        super(msg);
    }

    public DereferenceRuntimeException(String msg, Throwable t) {
        super(msg, t);
    }
}
