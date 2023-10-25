package ru.fusionsoft.dereferencer.load.loaders;

import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.load.SourceLoader;

public class GitHubLoader implements SourceLoader{

    @Override
    public boolean canLoad(URI uri) {
        // TODO
        return false;
    }

    @Override
    public JsonNode loadSource(URI uri) {
        // TODO
        return null;
    }
}
