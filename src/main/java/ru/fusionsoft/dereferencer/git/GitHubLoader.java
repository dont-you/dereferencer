package ru.fusionsoft.dereferencer.git;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.core.SourceLoader;

import java.net.URI;

public class GitHubLoader implements SourceLoader {
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