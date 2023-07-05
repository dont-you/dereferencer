package ru.fusionsoft.dereferencer.core.utils.load.impl;

import java.io.IOException;
import java.io.InputStream;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.utils.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.utils.load.SupportedSourceTypes;

public class URLLoader implements SourceLoader {

    @Override
    public InputStream getSource(Reference ref) throws LoadException {
        try {
            return ref.getAbsolute().toURL().openStream();
        } catch (IOException e) {
            // TODO
            throw new URIException("");
        }
    }

    @Override
    public SupportedSourceTypes getSourceType(Reference ref) throws LoadException {
        try {
            String contentType = ref.getAbsolute().toURL().openConnection().getContentType();
            return SupportedSourceTypes.resolveSourceType(contentType.substring(contentType.lastIndexOf("/") + 1));
        } catch (IOException e) {
            // TODO
            throw new URIException("");
        }
    }

}
