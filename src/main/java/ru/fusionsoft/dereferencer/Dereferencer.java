package ru.fusionsoft.dereferencer;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.FileFactory;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.LoaderFactory;
import ru.fusionsoft.dereferencer.core.URNPool;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;

public class Dereferencer {
    private final FileRegister fileRegister;

    public Dereferencer(URNPool urnPool, LoaderFactory loaderFactory, FileFactory fileFactory, URI defaultBaseURI) throws DereferenceException {
        if (urnPool==null && loaderFactory==null && fileFactory==null && defaultBaseURI==null)
            throw new DereferenceException("all constructor arguments must not be null");

        fileRegister = new FileRegister(urnPool, loaderFactory, fileFactory, defaultBaseURI);
    }

    public JsonNode dereference(URI uri) throws DereferenceException {
        return fileRegister.get(uri).getDerefedJson();
    }

    public JsonNode anonymousDereference(JsonNode jsonNode) throws DereferenceException {
        return fileRegister.get(jsonNode).getDerefedJson();
    }
}
