package ru.fusionsoft.dereferencer.core;

import java.net.URI;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

public interface URNPool {
    URI getLocator(URI urn) throws DereferenceException;
    void updateCache(URI uri, SourceLoader sourceLoader) throws DereferenceException;
}
