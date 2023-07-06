package ru.fusionsoft.dereferencer.core.utils.load;

import java.io.InputStream;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;

public interface SourceLoader {
    InputStream getSource(Reference ref) throws LoadException;

    SupportedSourceTypes getSourceType(Reference ref) throws LoadException;
}
