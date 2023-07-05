package ru.fusionsoft.dereferencer.core.exceptions;

public abstract class LoadException extends Exception {
    public LoadException(String msg) {
        super(msg);
    }

    public LoadException(Throwable t) {
        super(t);
    }
}
