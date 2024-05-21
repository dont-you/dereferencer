package ru.fusionsoft.dereferencer.core.load.urn;

import ru.fusionsoft.dereferencer.core.ResourceCenter;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;

public abstract class URNResolver {
    protected URNResolver nextResolver;

    public URNResolver() {
        nextResolver = null;
    }

    public final URNResolver setNext(URNResolver nextResolver) {
        this.nextResolver = nextResolver;
        return nextResolver;
    }

    protected final URI passToNextHandler(URI urn) throws DereferenceException {
        if (nextResolver == null)
            throw new DereferenceException("handler for the URN - " + urn + " is not defined");
        else
            return nextResolver.resolve(urn);
    }

    public final void update(URI uri, ResourceCenter resourceCenter){
        URNResolver currentResolver = nextResolver;
        while (currentResolver != null) {
            currentResolver.updatePool(uri, resourceCenter);
            currentResolver = currentResolver.nextResolver;
        }
    }

    protected abstract void updatePool(URI uri, ResourceCenter resourceCenter);

    public abstract URI resolve(URI urn) throws DereferenceException;
}
