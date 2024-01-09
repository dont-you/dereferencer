package ru.fusionsoft.dereferencer.core;

import java.net.URI;
import java.net.URL;

import org.jetbrains.annotations.Nullable;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

public interface URNPool {
    @Nullable URL getLocator(URI urn);

    @Nullable URL updateCache(URI uri, LoaderFactory loaderFactory) throws DereferenceException;
}
