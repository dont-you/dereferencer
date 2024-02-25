package ru.fusionsoft.dereferencer.core.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;

public class FileSystemLoader extends URLResourceLoader {
    @Override
    protected InputStream openStream(URI retrieval) throws IOException {
        File file = Paths.get(retrieval).toFile();
        return new FileInputStream(file);
    }

    @Override
    protected String getMimeType(URI retrieval) throws IOException {
        String path = retrieval.getPath();
        return path.substring(path.lastIndexOf(".") + 1);
    }

    @Override
    protected boolean canLoad(URI retrieval) {
        return retrieval.getHost() == null && !retrieval.getPath().isEmpty();
    }
}
