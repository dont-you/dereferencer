package ru.fusionsoft.dereferencer.core.impl.load;

import ru.fusionsoft.dereferencer.core.SourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;

public class FileLoader implements SourceLoader {
    @Override
    public boolean canLoad(URI uri) {
        return uri.getHost() == null && !uri.getPath().isEmpty();
    }

    @Override
    public InputStream loadSource(URI uri) throws IOException {
        File file = Paths.get(uri).toFile();
        return new FileInputStream(file);
    }

    @Override
    public String getMimeType(URI uri) {
        String path = uri.getPath();
        return path.substring(path.lastIndexOf(".") + 1);
    }
}
