package ru.fusionsoft.dereferencer.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tika.Tika;

public interface SourceLoader {
    boolean canLoad(URI uri);

    InputStream loadSource(URI uri) throws URISyntaxException, IOException;

    String getMimeType(URI uri) throws IOException;

}
