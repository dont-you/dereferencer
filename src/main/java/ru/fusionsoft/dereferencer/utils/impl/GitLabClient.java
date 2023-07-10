package ru.fusionsoft.dereferencer.utils.impl;

import java.io.InputStream;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.load.SupportedSourceTypes;

public class GitLabClient implements SourceLoader {

    private String token = null;

    @Override
    public InputStream getSource(Reference ref) throws LoadException {
        // TODO
        return null;
    }

    @Override
    public SupportedSourceTypes getSourceType(Reference ref) throws LoadException {
        // TODO
        return null;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
