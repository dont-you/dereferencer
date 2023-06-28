package ru.fusionsoft.dereferencer.core.routing.ref.impl;

import java.net.URI;

import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.utils.load.SourceLoader;

public class URLReference extends Reference {
    public URLReference(URI uri) throws URIException {
        super(uri);
        // TODO
    }

    public SourceLoader getSourceLoader(){
        return null;
    }
    // TODO

}
