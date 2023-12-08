package ru.fusionsoft.dereferencer.core;

import java.net.URI;

public interface URNPool {
    URI getLocator(URI urn);
    void updateCache(URI uri, SourceLoader sourceLoader);
}
