package ru.fusionsoft.dereferencer.core.load;

import java.net.URI;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.RetrievingException;
import ru.fusionsoft.dereferencer.core.load.impl.FileLoader;
import ru.fusionsoft.dereferencer.core.load.impl.URLLoader;

public class LoaderFactory implements ILoaderFactory {
    public LoaderFactory() {
    }

    @Override
    public SourceLoader getLoader(URI uri) throws LoadException {
        if (isFileSystemReference(uri))
            return new FileLoader(uri);
        else if (isURLReference(uri))
            return new URLLoader(uri);
        else
            throw new RetrievingException("source loader for resource with uri " + uri + " is not implemented");
    }

    private boolean isURLReference(URI uri) {
        return uri.getHost() != null;
    }

    private boolean isFileSystemReference(URI uri) {
        return uri.getHost() == null && !uri.getPath().equals("");
    }

}
