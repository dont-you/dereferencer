package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;

public interface SourceLoader {
    public boolean canLoad(URI uri);
    public JsonNode loadSource(URI uri);
}
