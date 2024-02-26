package ru.fusionsoft.dereferencer.core.load;

import ru.fusionsoft.dereferencer.core.Resource;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;

public interface Loader {
    Resource load(URI uri) throws DereferenceException;
}
