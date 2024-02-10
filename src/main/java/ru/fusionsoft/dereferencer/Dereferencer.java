package ru.fusionsoft.dereferencer;

import com.fasterxml.jackson.databind.JsonNode;

import org.jetbrains.annotations.NotNull;
import ru.fusionsoft.dereferencer.core.*;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;

public class Dereferencer {
    private final FileRegister fileRegister;

    public Dereferencer(@NotNull URNPool urnPool, @NotNull LoaderFactory loaderFactory, @NotNull FileFactory fileFactory, @NotNull TypeAdapter typeAdapter, @NotNull URI defaultBaseURI) {
        fileRegister = new FileRegister(urnPool, loaderFactory, fileFactory, typeAdapter, defaultBaseURI);
    }

    public JsonNode dereference(@NotNull URI uri) throws DereferenceException {
        return fileRegister.get(uri).getDerefedJson();
    }

    public JsonNode anonymousDereference(@NotNull JsonNode jsonNode) throws DereferenceException {
        return fileRegister.get(jsonNode).getDerefedJson();
    }
}
