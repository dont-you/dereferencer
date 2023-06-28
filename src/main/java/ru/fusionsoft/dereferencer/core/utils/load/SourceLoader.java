package ru.fusionsoft.dereferencer.core.utils.load;

import java.io.InputStream;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;

public interface SourceLoader {
    public InputStream getSource(Reference ref) throws DereferenceException;
    public SupportedSourceTypes getSourceType(Reference ref) throws DereferenceException;
}
