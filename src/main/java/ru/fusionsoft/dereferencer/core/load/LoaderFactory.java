package ru.fusionsoft.dereferencer.core.load;

import java.net.URI;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.RetrievingException;
import ru.fusionsoft.dereferencer.core.load.impl.FileLoader;
import ru.fusionsoft.dereferencer.core.load.impl.URLLoader;

public class LoaderFactory implements ILoaderFactory {
    private final FileLoader fileLoader;
    private final URLLoader urlLoader;

    public LoaderFactory() {
        fileLoader = new FileLoader();
        urlLoader = new URLLoader();
    }

    @Override
    public SourceLoader getLoader(URI uri) throws LoadException {
        if (isFileSystemReference(uri))
            return fileLoader;
        else if (isURLReference(uri))
            return urlLoader;
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
