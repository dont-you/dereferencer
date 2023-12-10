package ru.fusionsoft.dereferencer.core;

import java.net.URI;
import java.net.URL;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

public interface LoaderFactory {
    SourceLoader getSourceLoader(URL url) throws DereferenceException;
}
