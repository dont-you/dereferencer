package ru.fusionsoft.dereferencer.allof;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.core.DereferencedFile;
import ru.fusionsoft.dereferencer.core.DereferencedFileFactory;

import java.net.URI;

public class MergedFileFactory extends DereferencedFileFactory {
    @Override
    public DereferencedFile makeInstance(URI baseURI, JsonNode source){
        return new MergedFile(baseURI,source);
    }
}