package ru.fusionsoft.dereferencer.core.load;

import java.net.URI;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;

public interface ILoaderFactory{
    public SourceLoader getLoader(URI uri) throws LoadException;
}
