package ru.fusionsoft.dereferencer.core.load;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;
import java.net.URLConnection;

public interface Loader {
    URLConnection load(URI uri) throws DereferenceException;
}
