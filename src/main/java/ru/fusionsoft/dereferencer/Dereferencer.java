package ru.fusionsoft.dereferencer;

import com.fasterxml.jackson.databind.JsonNode;

import org.jetbrains.annotations.NotNull;
import ru.fusionsoft.dereferencer.core.FileFactory;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.LoaderFactory;
import ru.fusionsoft.dereferencer.core.URNPool;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;

public class Dereferencer {
    private final FileRegister fileRegister;

    public Dereferencer(@NotNull URNPool urnPool, @NotNull LoaderFactory loaderFactory, @NotNull FileFactory fileFactory, @NotNull URI defaultBaseURI) {
        fileRegister = new FileRegister(urnPool, loaderFactory, fileFactory, defaultBaseURI);
    }

    public JsonNode dereference(@NotNull URI uri) throws DereferenceException {
        return fileRegister.get(uri).getDerefedJson();
    }

    public JsonNode anonymousDereference(@NotNull JsonNode jsonNode) throws DereferenceException {
        return fileRegister.get(jsonNode).getDerefedJson();
    }
}
