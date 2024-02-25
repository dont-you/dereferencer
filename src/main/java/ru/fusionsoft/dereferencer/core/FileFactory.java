package ru.fusionsoft.dereferencer.core;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.InputStream;
import java.net.URI;

public interface FileFactory {
    File makeFile(FileRegister fileRegister, URI retrievalURI, InputStream source, String mimetype) throws DereferenceException;
}
