package ru.fusionsoft.dereferencer.core;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;

public interface ResourceCenter {
    URLConnection load(URI uri) throws IOException;
}
