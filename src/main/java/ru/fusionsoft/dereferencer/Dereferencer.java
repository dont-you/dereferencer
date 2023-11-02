package ru.fusionsoft.dereferencer;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.FileFactory;
import ru.fusionsoft.dereferencer.core.LoaderFactory;
import ru.fusionsoft.dereferencer.core.URNPool;

import java.net.URI;

public class Dereferencer {
    public Dereferencer(URNPool urnPool, LoaderFactory loaderFactory, FileFactory fileFactory, URI defaultBaseURI){
        // TODO
    }

    public JsonNode dereference(URI uri){
        // TODO
        return null;
    }

    public JsonNode anonymousDereference(JsonNode jsonNode){
        // TODO
        return null;
    }
}
