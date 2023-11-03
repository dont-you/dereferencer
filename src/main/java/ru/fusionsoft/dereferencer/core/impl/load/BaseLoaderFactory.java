package ru.fusionsoft.dereferencer.core.impl.load;

import ru.fusionsoft.dereferencer.core.LoaderFactory;
import ru.fusionsoft.dereferencer.core.SourceLoader;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;

public class BaseLoaderFactory implements LoaderFactory {

    private LocalLoader localLoader;
    private URLLoader urlLoader;

    @Override
    public SourceLoader getSourceLoader(URI uri) throws DereferenceException {
        if (localLoader.canLoad(uri))
            return localLoader;
        else if (urlLoader.canLoad(uri))
            return urlLoader;
        else
            throw new DereferenceException("source loader for resource with uri " + uri + " is not implemented");
    }
}
