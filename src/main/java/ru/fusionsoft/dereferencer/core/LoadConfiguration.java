package ru.fusionsoft.dereferencer.core;

import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.schema.SchemaNode;

public interface LoadConfiguration {
    LoadingFlag[] getLoadingFlags();

    int getCashSize();

    Map<Route, SchemaNode> getPreloadedSchemas();

    Tokens getTokens();

    Logger getLogger();

    URI getDefaultBaseUri();
}
