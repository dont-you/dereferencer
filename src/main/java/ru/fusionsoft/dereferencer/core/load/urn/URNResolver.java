package ru.fusionsoft.dereferencer.core.load.urn;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;

public abstract class URNResolver {
    protected URNResolver nextResolver;

    public URNResolver() {
        nextResolver = null;
    }

    public final URNResolver setNext(URNResolver nextResolver){
        this.nextResolver = nextResolver;
        return nextResolver;
    }

    protected final URI passToNextHandler(URI urn){
        if(nextResolver==null)
            //TODO
            throw new RuntimeException();
        else
            return nextResolver.resolve(urn);
    }

    public final void update(URI uri){
        URNResolver currentResolver = nextResolver;
        while (currentResolver!=null){
            currentResolver.updatePool(uri);
            currentResolver = currentResolver.nextResolver;
        }
    }

    protected abstract void updatePool(URI uri);
    public abstract URI resolve(URI urn);
}
