package ru.fusionsoft.dereferencer.core;

import java.net.URI;

public interface LoaderFactory {
    public SourceLoader getSourceLoader(URI uri);
}
