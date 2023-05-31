package ru.fusionsoft.dereferencer.core.load;

import com.google.common.cache.LoadingCache;

import ru.fusionsoft.dereferencer.DereferenceConfiguration;
import ru.fusionsoft.dereferencer.core.ref.Reference;
import ru.fusionsoft.dereferencer.core.ref.ReferenceFactory;
import ru.fusionsoft.dereferencer.core.schema.SchemaNode;

public class SchemaLoader {
    DereferenceConfiguration derefCfg;
    RetrievalManager retrievalManager;
    ReferenceFactory referenceFactory;
    LoadingCache<Reference, SchemaNode> cache;

    public SchemaLoader(DereferenceConfiguration derefCfg, RetrievalManager retrievalManager, ReferenceFactory referenceFactory){
        // TODO
    }

    public SchemaNode get(Reference reference){
        // TODO
        return null;
    }
}
