package ru.fusionsoft.dereferencer.allof;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.AbsoluteURI;
import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.ref.ReferenceManager;

public class AllOfFile extends File{
    AllOfFile(AbsoluteURI absoluteURI, JsonNode sourceNode, ReferenceManager referenceManager){
        super(absoluteURI, sourceNode, referenceManager);
    }

    @Override
    protected void resolveNode(String path, JsonNode node) {
        // TODO
        super.resolveNode(path, node);
    }

    @Override
    public JsonNode getDerefedJson() {
        // TODO
        return super.getDerefedJson();
    }
}
