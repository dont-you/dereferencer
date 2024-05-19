package ru.fusionsoft.dereferencer.core.load;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

public interface URLLoader {
    Resource load(URI uri) throws IOException, URISyntaxException;
}
