package ru.fusionsoft.dereferencer.core.reference;

import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.enums.ReferenceType;
import ru.fusionsoft.dereferencer.exception.ReferenceException;

public interface Reference {
    public ReferenceType getReferenceType();

    public URI getUri();

    public String getFragment();

    public JsonNode getSource() throws ReferenceException;

    public JsonNode setToSource(JsonNode setNode) throws ReferenceException;

    public Reference createNewReference(String uri) throws ReferenceException;
}
