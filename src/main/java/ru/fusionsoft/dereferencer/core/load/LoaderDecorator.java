package ru.fusionsoft.dereferencer.core.load;

import ru.fusionsoft.dereferencer.core.Resource;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public abstract class LoaderDecorator implements Loader {
    Loader loader;

    public LoaderDecorator(Loader loader) {
        this.loader = loader;
    }

    @Override
    public Resource load(URI uri) throws DereferenceException {
        if (canLoad(uri)) {
            Resource resource = new Resource(uri);
            try {
                resource.setStream(openStream(uri));
                resource.setMimetype(getMimeType(uri));
            } catch (IOException e) {
                throw new DereferenceException("errors when retrieving a source from " + uri + " by " + uri.getScheme() + " proto");
            }
            return resource;
        } else {
            return loader.load(uri);
        }
    }

    protected abstract boolean canLoad(URI uri);

    protected abstract String getMimeType(URI retrieval);

    protected abstract InputStream openStream(URI retrieval) throws IOException;
}
