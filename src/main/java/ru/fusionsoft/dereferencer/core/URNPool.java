package ru.fusionsoft.dereferencer.core;

import java.net.URI;

import org.jetbrains.annotations.Nullable;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

public interface URNPool {
    @Nullable URI getLocator(URI urn);

    @Nullable URI updateCache(URI uri, LoaderFactory loaderFactory) throws DereferenceException;
}
