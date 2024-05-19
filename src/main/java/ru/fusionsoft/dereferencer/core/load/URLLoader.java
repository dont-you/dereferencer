package ru.fusionsoft.dereferencer.core.load;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public interface URLLoader {
    Resource load(URI uri) throws IOException, URISyntaxException;
}
