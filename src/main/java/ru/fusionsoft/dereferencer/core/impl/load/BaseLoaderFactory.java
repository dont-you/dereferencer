package ru.fusionsoft.dereferencer.core.impl.load;

import ru.fusionsoft.dereferencer.core.LoaderFactory;
import ru.fusionsoft.dereferencer.core.SourceLoader;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;

public class BaseLoaderFactory implements LoaderFactory {

    private final FileLoader fileLoader;
    private final HTTPLoader HTTPLoader;

    public BaseLoaderFactory() {
        fileLoader = new FileLoader();
        HTTPLoader = new HTTPLoader();
    }

    @Override
    public SourceLoader getSourceLoader(URI uri) throws DereferenceException {
        if (fileLoader.canLoad(uri))
            return fileLoader;
        else if (HTTPLoader.canLoad(uri))
            return HTTPLoader;
        else
            throw new DereferenceException("source loader for resource with url " + uri + " is not implemented");
    }
}
