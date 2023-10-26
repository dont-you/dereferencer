package ru.fusionsoft.dereferencer.core.load;

import java.net.URI;

public interface LoaderFactory {
    public SourceLoader get(URI uri);
}
