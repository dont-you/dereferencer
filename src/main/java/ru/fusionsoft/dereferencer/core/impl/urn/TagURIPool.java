package ru.fusionsoft.dereferencer.core.impl.urn;

import ru.fusionsoft.dereferencer.core.SourceLoader;
import ru.fusionsoft.dereferencer.core.URNPool;

import java.net.URI;
import java.util.Map;

public class TagURIPool implements URNPool {
    Map<URN, URI> cache;
    @Override
    public URI getLocator(URI urn) {
        // TODO
        return null;
    }

    @Override
    public void updateCache(URI uri, SourceLoader sourceLoader) {
        // TODO
    }
}
