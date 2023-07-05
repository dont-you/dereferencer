package ru.fusionsoft.dereferencer.core;

import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.schema.ISchemaNode;

public interface LoadConfiguration {
    public LoadingFlag[] getLoadingFlags();

    public int getCashSize();

    public Map<Route, ISchemaNode> getPreloadedSchemas();

    public Tokens getTokens();

    public Logger getLogger();

    public URI getDefaultBaseUri();
}
