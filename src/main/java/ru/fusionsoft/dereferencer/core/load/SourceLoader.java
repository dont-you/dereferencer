package ru.fusionsoft.dereferencer.core.load;

import java.io.InputStream;
import java.net.URI;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;

public interface SourceLoader {
    InputStream getSource(URI uri) throws LoadException;

    SupportedSourceTypes getSourceType(URI uri) throws LoadException;
}
