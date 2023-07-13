package ru.fusionsoft.dereferencer.utils.impl;

import java.io.InputStream;
import java.net.URI;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.load.SupportedSourceTypes;

public class GitLabSourceLoader implements SourceLoader{

    private String token = null;

    @Override
    public InputStream getSource(URI uri) throws LoadException {
        // TODO
        return null;
    }

    @Override
    public SupportedSourceTypes getSourceType(URI uri) throws LoadException {
        // TODO
        return null;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
