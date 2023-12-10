package ru.fusionsoft.dereferencer.core.impl.load;

import ru.fusionsoft.dereferencer.core.SourceLoader;

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
    public InputStream loadSource(URL url) throws IOException {
        return url.openStream();
    }

    @Override
    public SourceType getSourceType(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        return SourceType.resolveSourceTypeByMimeType(connection.getContentType());
    }
}
