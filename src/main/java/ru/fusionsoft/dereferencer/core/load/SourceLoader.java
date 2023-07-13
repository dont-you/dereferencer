package ru.fusionsoft.dereferencer.core.load;

import java.io.InputStream;
import java.net.URI;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;

public interface SourceLoader {
    InputStream getSource() throws LoadException;

    SupportedSourceTypes getSourceType() throws LoadException;
}
