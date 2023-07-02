package ru.fusionsoft.dereferencer.core.utils.load.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.utils.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.utils.load.SupportedSourceTypes;

public class FileLoader implements SourceLoader {

    @Override
    public InputStream getSource(Reference ref) throws DereferenceException {
        File file = Paths.get(ref.getAbsolute()).toAbsolutePath().toFile();
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            // TODO
            throw new URIException("");
        }
    }

    @Override
    public SupportedSourceTypes getSourceType(Reference ref) {
        String path = Paths.get(ref.getAbsolute()).toString();
        return SupportedSourceTypes.resolveSourceType(path.substring(path.lastIndexOf(".") + 1));
    }
}
