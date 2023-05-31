package ru.fusionsoft.dereferencer.core.ref;

import java.net.URI;

public class ReferenceFactory{

    private URI defaultBaseUri;

    public ReferenceFactory(URI defaultBaseUri) {
        this.defaultBaseUri = defaultBaseUri;
    }

    public Reference create(URI uri){
        // TODO
        return null;
    }

    public Reference create(String uri){
        // TODO
        return null;
    }

    public Reference referenceResolution(Reference reference, String target){
        // TODO
        return null;
    }

    public Reference referenceResolution(Reference reference, URI target){
        // TODO
        return null;
    }
}
