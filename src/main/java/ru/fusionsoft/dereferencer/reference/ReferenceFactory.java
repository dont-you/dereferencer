package ru.fusionsoft.dereferencer.reference;

import java.net.URI;

import ru.fusionsoft.dereferencer.exception.ReferenceException;
import ru.fusionsoft.dereferencer.reference.impl.RemoteReference;
import ru.fusionsoft.dereferencer.reference.impl.URLReference;

public class ReferenceFactory{
    public static Reference create(URI uri) throws ReferenceException{
        if(ReferenceType.isURLReference(uri))
            return new URLReference(uri);
        else if(ReferenceType.isRemoteReference(uri))
            return new RemoteReference(uri);
        else
            throw new ReferenceException("failed to recognize link");

    }
}
