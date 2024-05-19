package ru.fusionsoft.dereferencer.core.load;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

public class Resource {
    private final URI retrievalURI;
    private final InputStream inputStream;

    public Resource(URLConnection urlConnection) throws IOException, URISyntaxException {
        this(urlConnection.getURL().toURI(), urlConnection.getInputStream());
    }

    public Resource(URI retrievalURI, InputStream inputStream) {
        this.retrievalURI = retrievalURI;
        this.inputStream = inputStream;
    }

    public URI getRetrievalURI() {
        return retrievalURI;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
