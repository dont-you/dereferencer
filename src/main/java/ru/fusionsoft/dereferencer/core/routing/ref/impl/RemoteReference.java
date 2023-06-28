package ru.fusionsoft.dereferencer.core.routing.ref.impl;

import java.net.URI;

import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.utils.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.utils.load.impl.FileLoader;

public class RemoteReference extends Reference {
    public RemoteReference(URI uri) throws URIException {
        super(uri);
    }

    public SourceLoader getSourceLoader(){
        return FileLoader.getInstance();
    }
}
