package ru.fusionsoft.dereferencer.core.impl.load;

import ru.fusionsoft.dereferencer.core.LoaderFactory;
import ru.fusionsoft.dereferencer.core.SourceLoader;

import java.net.URI;

public class BaseLoaderFactory implements LoaderFactory {

    @Override
    public SourceLoader getSourceLoader(URI uri) {
        // TODO
        return null;
    }
}
