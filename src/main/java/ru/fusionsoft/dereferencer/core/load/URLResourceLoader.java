package ru.fusionsoft.dereferencer.core.load;

import ru.fusionsoft.dereferencer.core.Resource;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public abstract class URLResourceLoader {
    protected URLResourceLoader nextLoader;

    public URLResourceLoader() {
        this.nextLoader = null;
    }

    public URLResourceLoader setNextLoader(URLResourceLoader nextLoader) {
        if (this.nextLoader != null)
            this.nextLoader.setNextLoader(nextLoader);
        else
            this.nextLoader = nextLoader;

        return this;
    }

    public final void load(Resource resource) throws DereferenceException {
        if (canLoad(resource.getRetrieval())) {
            executeLoading(resource);
        } else {
            if (nextLoader == null)
                throw new DereferenceException("there is no suitable URL resource loader for the - " + resource.getRetrieval());
            else
                nextLoader.load(resource);
        }
    }

    protected void executeLoading(Resource resource) throws DereferenceException {
        URI retrieval = resource.getRetrieval();
        try {
            resource.setStream(openStream(retrieval));
            resource.setMimetype(getMimeType(retrieval));
        } catch (IOException e) {
            throw new DereferenceException("errors when retrieving a source from " + retrieval);
        }
    }

    protected abstract InputStream openStream(URI retrieval) throws IOException;

    protected abstract String getMimeType(URI retrieval) throws IOException;

    protected abstract boolean canLoad(URI retrieval);

    @Override
    public boolean equals(Object obj) {
        return this.getClass() == obj.getClass();
    }
}
