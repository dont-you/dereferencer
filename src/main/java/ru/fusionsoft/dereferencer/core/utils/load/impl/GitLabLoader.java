package ru.fusionsoft.dereferencer.core.utils.load.impl;

import java.io.InputStream;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.utils.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.utils.load.SupportedSourceTypes;

public class GitLabLoader implements SourceLoader{

    private String token=null;

    @Override
    public InputStream getSource(Reference ref) throws DereferenceException {
        // TODO
        return null;
    }

    @Override
    public SupportedSourceTypes getSourceType(Reference ref) throws DereferenceException {
        // TODO
        return null;
    }

    public void setToken(String token){
        this.token = token;
    }
}
