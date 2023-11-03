package ru.fusionsoft.dereferencer.core;

import java.net.URI;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

public interface LoaderFactory {
    public SourceLoader getSourceLoader(URI uri) throws DereferenceException;
}
