package ru.fusionsoft.dereferencer.core.impl.load;

import ru.fusionsoft.dereferencer.core.LoaderFactory;
import ru.fusionsoft.dereferencer.core.SourceLoader;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URL;

public class BaseLoaderFactory implements LoaderFactory {

    private final FileLoader fileLoader;
    private final HTTPLoader HTTPLoader;

    public BaseLoaderFactory(){
        fileLoader = new FileLoader();
        HTTPLoader = new HTTPLoader();
    }

    @Override
    public SourceLoader getSourceLoader(URL url) throws DereferenceException {
        if (fileLoader.canLoad(url))
            return fileLoader;
        else if (HTTPLoader.canLoad(url))
            return HTTPLoader;
        else
            throw new DereferenceException("source loader for resource with url " + url + " is not implemented");
    }
}
