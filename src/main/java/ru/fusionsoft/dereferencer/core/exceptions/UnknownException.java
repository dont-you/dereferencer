package ru.fusionsoft.dereferencer.core.exceptions;

public class UnknownException extends LoadException {
    public UnknownException(String msg) {
        super(msg);
    }

    public UnknownException(Throwable t) {
        super(t);
    }
}
