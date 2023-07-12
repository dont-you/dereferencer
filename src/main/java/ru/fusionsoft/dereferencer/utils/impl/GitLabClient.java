package ru.fusionsoft.dereferencer.utils.impl;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.utils.SourceClient;
import ru.fusionsoft.dereferencer.core.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.load.SupportedSourceTypes;

public class GitLabClient implements SourceLoader, SourceClient {

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

    @Override
    public List<String> directoryList(URI uri) {
        // TODO Auto-generated method stub
        return new ArrayList<>();
    }
}
