package ru.fusionsoft.dereferencer.core;

import java.net.URI;

public interface URNPool {
    public URI getLocator(URI urn);
    public void updateCache(URI uri, LoaderFactory loaderFactory);
}
