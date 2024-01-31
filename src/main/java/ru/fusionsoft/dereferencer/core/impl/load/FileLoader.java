package ru.fusionsoft.dereferencer.core.impl.load;

import ru.fusionsoft.dereferencer.core.SourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class FileLoader implements SourceLoader {
    @Override
    public boolean canLoad(URL url) {
        return url.getHost().isEmpty() && !url.getPath().isEmpty();
    }

    @Override
    public InputStream loadSource(URL url) throws URISyntaxException, IOException {
        File file = Paths.get(url.toURI()).toFile();
        return new FileInputStream(file);
    }

    @Override
    public SourceType getSourceType(URL url) {
        return SourceType.resolveSourceTypeByPath(url.getPath());
    }
}
