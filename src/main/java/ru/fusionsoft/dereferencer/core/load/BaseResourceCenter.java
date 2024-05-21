package ru.fusionsoft.dereferencer.core.load;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.fusionsoft.dereferencer.core.ResourceCenter;
import ru.fusionsoft.dereferencer.core.load.urn.URNResolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class BaseResourceCenter implements ResourceCenter {
    private URLLoader urlLoader;
    private URNResolver urnResolver;

    public BaseResourceCenter(@NotNull URLLoader urlLoader) {
        this.urlLoader = urlLoader;
        urnResolver = null;
    }

    public BaseResourceCenter(@NotNull URLLoader urlLoader, @Nullable URNResolver urnResolver) {
        this.urlLoader = urlLoader;
        this.urnResolver = urnResolver;
    }

    @Override
    public Resource load(URI uri) throws IOException, URISyntaxException {
        if (urnResolver == null)
            return urlLoader.load(uri);

        if (uri.getScheme() != null && uri.getScheme().equals("urn")) {
            uri = urnResolver.resolve(uri);
        } else {
            urnResolver.update(uri, this);
        }

        return urlLoader.load(uri);
    }

    public InputStream loadOnlyStream(URI uri) throws IOException, URISyntaxException {
        return urlLoader.load(uri).getInputStream();
    }

    public URLLoader getLoader() {
        return urlLoader;
    }

    public void setLoader(URLLoader URLLoader) {
        this.urlLoader = URLLoader;
    }

    public URNResolver getURNResolver() {
        return this.urnResolver;
    }

    public void setURNResolver(URNResolver urnResolver) {
        this.urnResolver = urnResolver;
    }
}
