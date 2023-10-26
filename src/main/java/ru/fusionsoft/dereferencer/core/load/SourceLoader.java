package ru.fusionsoft.dereferencer.core.load;

import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;

public interface SourceLoader {
    public JsonNode loadSource(URI uri);
    public boolean canLoad(URI uri);
}
