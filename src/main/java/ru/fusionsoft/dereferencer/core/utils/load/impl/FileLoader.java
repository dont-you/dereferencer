package ru.fusionsoft.dereferencer.core.utils.load.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tika.Tika;
import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.RetrievingException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.utils.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.utils.load.SupportedSourceTypes;

public class FileLoader implements SourceLoader {

    @Override
    public InputStream getSource(Reference ref) throws LoadException {
        File file = Paths.get(ref.getAbsolute()).toAbsolutePath().toFile();
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RetrievingException("file not found, path - " + file.getAbsolutePath());
        }
    }

    @Override
    public SupportedSourceTypes getSourceType(Reference ref) throws LoadException {
        Path path = Paths.get(ref.getAbsolute());
        try {
            Tika tika = new Tika();
            return SupportedSourceTypes.resolveSourceTypeByMimeType(tika.detect(path));
        } catch (IOException e) {
            throw new UnknownException(
                    "unknown exception caused while getting mime type with msg - " + e.getMessage());
        }
    }
}
