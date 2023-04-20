package ru.fusionsoft.dereferencer.core.reference;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.enums.ReferenceType;
import ru.fusionsoft.dereferencer.exception.ReferenceException;

public interface Reference{
    public ReferenceType getReferenceType();
    public JsonNode getSource() throws ReferenceException;
    public Reference createUsingCurrent(String newPath) throws ReferenceException;
}