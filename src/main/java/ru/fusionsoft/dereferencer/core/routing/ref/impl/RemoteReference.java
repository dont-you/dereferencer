package ru.fusionsoft.dereferencer.core.routing.ref.impl;

import java.io.InputStream;
import java.net.URI;

import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;

public class RemoteReference extends Reference {
    public RemoteReference(URI uri) throws URIException {
        super(uri);
        // TODO
    }

    public InputStream getSource() {
        // TODO
        return null;
    }

    public String getSourceType() {
        // TODO
        return null;
    }

    // TODO

}
