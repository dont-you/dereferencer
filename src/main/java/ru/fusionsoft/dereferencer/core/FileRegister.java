package ru.fusionsoft.dereferencer.core;

import org.jetbrains.annotations.NotNull;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.load.BaseResourceCenterBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class FileRegister {
    private final ResourceCenter resourceCenter;
    private final FileFactory fileFactory;
    private final URI defaultBaseURI;
    private final Map<URI, File> cache;

    public FileRegister(FileFactory fileFactory, URI defaultBaseURI) {
        this.fileFactory = fileFactory;
        this.defaultBaseURI = defaultBaseURI;
        this.resourceCenter = BaseResourceCenterBuilder.getInstance().build();
        cache = new TreeMap<>();
    }

    public File get(@NotNull URI uri) throws DereferenceException {
        uri = defaultBaseURI.resolve(uri);
        File lookingFile = cache.get(uri);

        if (lookingFile != null)
            return lookingFile;

        return makeFile(uri);

    }

    private File makeFile(URI uri) throws DereferenceException {
        Resource resource = resourceCenter.load(uri);
        File lookingFile = fileFactory.makeFile(this, resource.getRetrieval(), resource.getStream(), resource.getMimetype());

        if (cache.containsKey(lookingFile.getBaseURI()))
            return cache.get(lookingFile.getBaseURI());

        updateCache(resource.getRetrieval(), resource.getDuplicates(), lookingFile);
        lookingFile.resolve();
        return lookingFile;
    }

    private void updateCache(URI retrieval, URI[] duplicates, File file){
        cache.put(retrieval, file);
        Arrays.stream(duplicates).forEach(dup -> cache.put(dup, file));
    }
}
