package ru.fusionsoft.dereferencer.core.load;

import ru.fusionsoft.dereferencer.core.Resource;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

public class HTTPLoader extends URLResourceLoader {
    @Override
    public boolean canLoad(URI uri) {
        return uri.getScheme().equals("http") || uri.getScheme().equals("https");
    }

    @Override
    public void executeLoading(Resource resource) throws DereferenceException {
        URI retrieval = resource.getRetrieval();
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) resource.getRetrieval().toURL().openConnection();
            httpURLConnection.connect();
            resource.setStream(httpURLConnection.getInputStream());
            resource.updateRetrieval(httpURLConnection.getURL().toURI());
            resource.setMimetype(httpURLConnection.getContentType());
        } catch (IOException | URISyntaxException e) {
            throw new DereferenceException("errors when retrieving a source from " + retrieval + " by http proto");
        }
    }

    @Override
    protected InputStream openStream(URI retrieval) throws IOException {
        return retrieval.toURL().openStream();
    }

    @Override
    protected String getMimeType(URI uri) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("HEAD");
        return connection.getContentType();
    }

}
