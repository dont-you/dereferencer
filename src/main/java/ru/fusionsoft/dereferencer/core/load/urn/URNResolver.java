package ru.fusionsoft.dereferencer.core.load.urn;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;

public interface URNResolver {
    void updatePool(URI uri) throws DereferenceException;

    URI resolve(URI urn) throws DereferenceException;
}
