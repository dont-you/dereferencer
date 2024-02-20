package ru.fusionsoft.dereferencer;

import com.fasterxml.jackson.databind.JsonNode;

import org.jetbrains.annotations.NotNull;
import ru.fusionsoft.dereferencer.core.*;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;

public class Dereferencer {
    private final FileRegister fileRegister;

    public Dereferencer(@NotNull FileFactory fileFactory, @NotNull URI defaultBaseURI) {
        fileRegister = new FileRegister(fileFactory, defaultBaseURI);
    }

    public JsonNode dereference(@NotNull URI uri) throws DereferenceException {
        return fileRegister.get(uri).getDerefedJson();
    }

}
