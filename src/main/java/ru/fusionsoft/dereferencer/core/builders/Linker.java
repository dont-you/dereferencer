package ru.fusionsoft.dereferencer.core.builders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.reference.Reference;
import ru.fusionsoft.dereferencer.exception.ReferenceException;

public class Linker {
    public static JsonNode combine(Reference reference)
            throws ReferenceException {
        try {
            if (reference.getSource().isMissingNode()) {
                if (reference.getFragment().equals(""))
                    throw new ReferenceException(
                            "could not resolve missed ref with uri - '" + reference.getUri() + "'");

                String newFragment = reference.getFragment().substring(0, reference.getFragment().lastIndexOf("/"));
                combine(reference.createNewReference("#" + newFragment));
            }
            JsonNode currentNode = Dereferencer.objectMapper.readTree("{}");
            SchemeResolver schemeResolver = new SchemeResolver(reference);
            ((ObjectNode) currentNode).set("resultOfDereference",
                    schemeResolver.dereferenceResolve(reference.getSource()));
            return reference.setToSource(currentNode.get("resultOfDereference"));
        } catch (JsonProcessingException e) {
            throw new ReferenceException("some internal error");
        }
    }
}
