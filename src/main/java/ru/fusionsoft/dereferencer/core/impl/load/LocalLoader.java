package ru.fusionsoft.dereferencer.core.impl.load;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.tika.Tika;

import ru.fusionsoft.dereferencer.core.SourceLoader;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

public class LocalLoader implements SourceLoader {
    private Tika tika;

    LocalLoader(){
        tika = new Tika();
    }

    @Override
    public boolean canLoad(URI uri) {
        return uri.getHost() == null && !uri.getPath().equals("");
    }

    @Override
    public JsonNode loadSource(URI uri) throws DereferenceException {
        try{
            File file = Paths.get(uri.normalize()).toFile();
            SourceType sourceType = getSourceType(file);

            return SourceLoader.makeJsonFromInputStream(new FileInputStream(file), sourceType);
        } catch (IOException | DereferenceException e) {
            throw new DereferenceException("could not load source from " + uri);
        }
    }

    private SourceType getSourceType(File file) throws IOException{
        return SourceType.resolveSourceTypeByMimeType(tika.detect(file));
    }
}
