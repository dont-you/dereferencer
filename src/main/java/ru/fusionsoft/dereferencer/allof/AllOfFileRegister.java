package ru.fusionsoft.dereferencer.allof;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.AbsoluteURI;
import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.ref.ReferenceManager;
import ru.fusionsoft.dereferencer.load.LoaderFactory;

public class AllOfFileRegister extends FileRegister{

    public AllOfFileRegister(LoaderFactory loaderFactory, ReferenceManager referenceManager){
        // TODO
        super(loaderFactory, referenceManager);
    }


    @Override
    protected File makeFile(AbsoluteURI absoluteURI, JsonNode sourceNode) {
        // TODO
        return super.makeFile(absoluteURI, sourceNode);
    }

}
