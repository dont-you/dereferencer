package ru.fusionsoft.dereferencer.core;

import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.ref.ReferenceManager;
import ru.fusionsoft.dereferencer.load.LoaderFactory;

public class FileRegister {
    public FileRegister(LoaderFactory loaderFactory, ReferenceManager referenceManager){
        // TODO
    }

    public final File get(URI uri){
        // TODO
        return null;
    }

    public final File getAnonymous(JsonNode node){
        // TODO
        return null;
    }

    protected File makeFile(AbsoluteURI absoluteURI, JsonNode sourceNode){
        // TODO
        return null;
    }
}
