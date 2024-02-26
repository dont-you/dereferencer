package ru.fusionsoft.dereferencer.core.load.urn;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.load.BaseResourceCenter;

import java.io.InputStream;
import java.net.URI;

public abstract class URNResolver {
    private BaseResourceCenter baseResourceCenter;

    protected InputStream load(URI uri) throws DereferenceException {
        return baseResourceCenter.loadOnlyStream(uri);
    }
    public void setBaseResourceCenter(BaseResourceCenter baseResourceCenter){
        this.baseResourceCenter = baseResourceCenter;
    }

    public abstract void updatePool(URI uri) throws DereferenceException;
    public abstract URI resolve(URI urn) throws DereferenceException;
}
