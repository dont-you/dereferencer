package ru.fusionsoft.dereferencer.core.load.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;

import org.apache.tika.Tika;
import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.RetrievingException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;
import ru.fusionsoft.dereferencer.core.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.load.SupportedSourceTypes;

public class FileLoader implements SourceLoader {

    private URI uri;

    public FileLoader(URI uri) {
        this.uri = uri;
    }

    @Override
    public InputStream getSource() throws LoadException {
        File file = Paths.get(uri.normalize()).toFile();
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RetrievingException("file not found, path - " + file.getAbsolutePath());
        }
    }

    @Override
    public SupportedSourceTypes getSourceType() throws LoadException {
        File file = Paths.get(uri.normalize()).toFile();
        try {
            Tika tika = new Tika();
            return SupportedSourceTypes.resolveSourceTypeByMimeType(tika.detect(file));
        } catch (IOException e) {
            throw new UnknownException(
                    "unknown exception caused while getting mime type with msg - " + e.getMessage());
        }
    }
}
