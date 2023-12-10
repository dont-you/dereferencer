package ru.fusionsoft.dereferencer.core.impl.load;

import ru.fusionsoft.dereferencer.core.SourceLoader;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPLoader implements SourceLoader {
    HTTPLoader(){
    }

    @Override
    public boolean canLoad(URL url) {
        return url.getHost() != null;
    }

    @Override
    public InputStream loadSource(URL url) throws DereferenceException {
        try{
            return url.openStream();
        } catch (IOException e) {
            throw new DereferenceException("could not load source from " + url);
        }
    }

    @Override
    public SourceType getSourceType(URL url) throws DereferenceException {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            return SourceType.resolveSourceTypeByMimeType(connection.getContentType());
        } catch (IOException e) {
            throw new DereferenceException("could not determine source type by url " + url);
        }
    }
}
