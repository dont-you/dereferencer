package ru.fusionsoft.dereferencer.urn;

import java.net.URI;

import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.ref.Reference;
import ru.fusionsoft.dereferencer.core.ref.ReferenceManager;

public class URNReferenceManager extends ReferenceManager{

    @Override
    protected Reference makeReference(URI uri, File file) {
        // TODO
        return super.makeReference(uri, file);
    }

}
