package ru.fusionsoft.dereferencer.git;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.core.SourceLoader;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class GitHubLoader implements SourceLoader {

    @Override
    public boolean canLoad(URL url) {
        return false;
    }

    @Override
    public InputStream loadSource(URL url) throws DereferenceException {
        return null;
    }

    @Override
    public SourceType getSourceType(URL url) throws DereferenceException {
        return null;
    }
}