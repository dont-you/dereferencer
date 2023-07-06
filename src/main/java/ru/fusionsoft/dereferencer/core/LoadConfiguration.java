package ru.fusionsoft.dereferencer.core;

import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.schema.ISchemaNode;

public interface LoadConfiguration {
    LoadingFlag[] getLoadingFlags();

    int getCashSize();

    Map<Route, ISchemaNode> getPreloadedSchemas();

    Tokens getTokens();

    Logger getLogger();

    URI getDefaultBaseUri();
}
