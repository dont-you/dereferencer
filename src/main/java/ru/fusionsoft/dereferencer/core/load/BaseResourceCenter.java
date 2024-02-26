package ru.fusionsoft.dereferencer.core.load;

import ru.fusionsoft.dereferencer.core.Resource;
import ru.fusionsoft.dereferencer.core.ResourceCenter;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.load.urn.URNResolver;

import java.io.InputStream;
import java.net.URI;

public class BaseResourceCenter implements ResourceCenter {
    private Loader loader;
    private URNResolver urnResolver;

    BaseResourceCenter() {
        loader = null;
        urnResolver = null;
    }

    @Override
    public Resource load(URI uri) throws DereferenceException {
        if (uri.getScheme() != null && uri.getScheme().equals("urn")) {
            URI updated = urnResolver.resolve(uri);
            Resource resource = loader.load(updated);
            resource.addDuplicate(uri);
            urnResolver.updatePool(updated);
            return resource;
        } else {
            Resource resource = loader.load(uri);
            urnResolver.updatePool(uri);
            return resource;
        }
    }

    public InputStream loadOnlyStream(URI uri) throws DereferenceException {
        return loader.load(uri).getStream();
    }

    public Loader getLoader() {
        return loader;
    }

    public void setLoader(Loader loader) {
        this.loader = loader;
    }

    public void setURNResolver(URNResolver urnResolver) {
        this.urnResolver = urnResolver;
    }

    public URNResolver getURNResolver() {
        return this.urnResolver;
    }
}
