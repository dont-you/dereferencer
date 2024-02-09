package ru.fusionsoft.dereferencer.core.impl.load;

import ru.fusionsoft.dereferencer.core.SourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;

public class HTTPLoader implements SourceLoader {
    HTTPLoader() {
    }

    @Override
    public boolean canLoad(URI uri) {
        return uri.getHost() != null;
    }

    @Override
    public InputStream loadSource(URI uri) throws IOException {
        return uri.toURL().openStream();
    }

    @Override
    public SourceType getSourceType(URI uri) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("HEAD");
        return SourceType.resolveSourceTypeByMimeType(connection.getContentType());
    }
}
