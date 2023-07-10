package ru.fusionsoft.dereferencer.core;

import java.net.URI;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import ru.fusionsoft.dereferencer.core.load.ILoaderFactory;
import ru.fusionsoft.dereferencer.core.load.LoaderFactory;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.schema.ISchemaNode;

public class LoadConfiguration {
    protected LoadingFlag[] loadingFlags;

    protected int cashSize;

    protected ILoaderFactory loaderFactory;

    protected Map<Route, ISchemaNode> preloadedSchemas;

    protected Logger logger;

    protected URI defaultBaseUri;

    public LoadConfiguration(){
        loadingFlags = new LoadingFlag[]{};
        cashSize = -1;
        loaderFactory = new LoaderFactory();
        preloadedSchemas = new HashMap<>();
        logger = logger.getGlobal();
        defaultBaseUri = Paths.get("./").toAbsolutePath().toUri();
    }

    public LoadingFlag[] getLoadingFlags() {
        return loadingFlags;
    }

    public void setLoadingFlags(LoadingFlag[] loadingFlags) {
        this.loadingFlags = loadingFlags;
    }

    public int getCashSize() {
        return cashSize;
    }

    public void setCashSize(int cashSize) {
        this.cashSize = cashSize;
    }

    public ILoaderFactory getLoaderFactory() {
        return loaderFactory;
    }

    public Map<Route, ISchemaNode> getPreloadedSchemas() {
        return preloadedSchemas;
    }

    public void setPreloadedSchemas(Map<Route, ISchemaNode> preloadedSchemas) {
        this.preloadedSchemas = preloadedSchemas;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public URI getDefaultBaseUri() {
        return defaultBaseUri;
    }

    public void setDefaultBaseUri(URI defaultBaseUri) {
        this.defaultBaseUri = defaultBaseUri;
    }


}
