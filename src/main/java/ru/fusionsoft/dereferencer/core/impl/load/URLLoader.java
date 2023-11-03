package ru.fusionsoft.dereferencer.core.impl.load;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.SourceLoader;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;

public class URLLoader implements SourceLoader {
    URLLoader(){
    }

    @Override
    public boolean canLoad(URI uri) {
        return uri.getHost() != null;
    }

    @Override
    public JsonNode loadSource(URI uri) throws DereferenceException {
        try{
            InputStream stream = uri.toURL().openStream();
            SourceType sourceType = getSourceType(uri);

            return SourceLoader.makeJsonFromInputStream(stream, sourceType);
        } catch (IOException | DereferenceException e) {
            throw new DereferenceException("could not load source from " + uri);
        }
    }

    private SourceType getSourceType(URI uri) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("HEAD");
        return SourceType.resolveSourceTypeByMimeType(connection.getContentType());
    }
}
