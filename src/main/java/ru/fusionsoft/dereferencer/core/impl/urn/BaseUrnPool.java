package ru.fusionsoft.dereferencer.core.impl.urn;

import ru.fusionsoft.dereferencer.core.LoaderFactory;
import ru.fusionsoft.dereferencer.core.URNPool;

import java.net.URI;

public class BaseUrnPool implements URNPool {
    @Override
    public URI getLocator(URI urn) {
        // TODO
        return null;
    }

    @Override
    public void updateCache(URI uri, LoaderFactory loaderFactory) {
        // TODO
    }
}
