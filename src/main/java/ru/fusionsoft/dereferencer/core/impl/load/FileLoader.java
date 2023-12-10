package ru.fusionsoft.dereferencer.core.impl.load;

import org.apache.tika.Tika;

import ru.fusionsoft.dereferencer.core.SourceLoader;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class FileLoader implements SourceLoader {
    private final Tika tika;

    FileLoader(){
        tika = new Tika();
    }

    @Override
    public boolean canLoad(URL url) {
        return url.getHost().equals("") && !url.getPath().equals("");
    }

    @Override
    public InputStream loadSource(URL url) throws DereferenceException {
        try{
            File file = Paths.get(url.toURI()).toFile();
            SourceType sourceType = getSourceType(file);

            return new FileInputStream(file);
        } catch (IOException | URISyntaxException e) {
            throw new DereferenceException("could not load source from " + url);
        }
    }

    @Override
    public SourceType getSourceType(URL url) throws DereferenceException {
        try {
            return getSourceType(Paths.get(url.toURI()).toFile());
        } catch (URISyntaxException | IOException e) {
            throw new DereferenceException("could not determine source type by url " + url);
        }
    }


    private SourceType getSourceType(File file) throws IOException{
        return SourceType.resolveSourceTypeByMimeType(tika.detect(file));
    }
}
