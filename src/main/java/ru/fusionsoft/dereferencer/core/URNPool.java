package ru.fusionsoft.dereferencer.core;

import java.net.URI;
import java.net.URL;

import org.jetbrains.annotations.Nullable;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

public interface URNPool {
    URI getLocator(URI urn) throws DereferenceException;

    @Nullable URL updateCache(URI uri, LoaderFactory loaderFactory) throws DereferenceException;
}
