package ru.fusionsoft.dereferencer.load;

import java.net.URI;

public interface LoaderFactory {
    public SourceLoader get(URI uri);
}
