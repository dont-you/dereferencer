package ru.fusionsoft.dereferencer.core;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.load.Resource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

public interface ResourceCenter {
    Resource load(URI uri) throws IOException, URISyntaxException;
}
