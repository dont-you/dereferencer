package ru.fusionsoft.dereferencer.core.load.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;
import ru.fusionsoft.dereferencer.core.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.load.SupportedSourceTypes;

public class URLLoader implements SourceLoader {

    @Override
    public InputStream getSource(URI uri) throws LoadException {
        try {
            return uri.toURL().openStream();
        } catch (IOException e) {
            throw new UnknownException(
                    "unknown exception caused while getting source with msg - " + e.getMessage());
        }
    }

    @Override
    public SupportedSourceTypes getSourceType(URI uri) throws LoadException {
        try {
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("HEAD");
            return SupportedSourceTypes.resolveSourceTypeByMimeType(connection.getContentType());
        } catch (IOException e) {
            throw new UnknownException(
                    "unknown exception caused while getting mime type with msg - " + e.getMessage());
        }
    }

}
