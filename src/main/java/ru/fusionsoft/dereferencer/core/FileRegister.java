package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class FileRegister {
    private final URNPool urnPool;
    private final LoaderFactory loaderFactory;
    private final FileFactory fileFactory;
    private final URI defaultBaseURI;
    private final Map<BaseURI, File> cache;

    public FileRegister(URNPool urnPool, LoaderFactory loaderFactory, FileFactory fileFactory, URI defaultBaseURI) {
        this.urnPool = urnPool;
        this.loaderFactory = loaderFactory;
        this.fileFactory = fileFactory;
        this.defaultBaseURI = defaultBaseURI;
        cache = new HashMap<>();
    }

    public File get(URI uri) throws DereferenceException {
        BaseURI fileBaseURI = new BaseURI(defaultBaseURI, uri);
        File lookingFile = cache.get(fileBaseURI);

        if (lookingFile != null)
            return lookingFile;

        if (fileBaseURI.getCanonical().getScheme().equals("urn")) {
            fileBaseURI.updateCanonical(urnPool.getLocator(fileBaseURI.getCanonical()));

            lookingFile = cache.get(fileBaseURI);

            if (lookingFile != null)
                return lookingFile;
        }

        JsonNode sourceJson = loaderFactory.getSourceLoader(fileBaseURI.getCanonical())
                .loadSource(fileBaseURI.getCanonical());

        try {
            URI idFieldURI = getIdField(sourceJson);

            if (idFieldURI != null)
                fileBaseURI.updateCanonical(idFieldURI);

            lookingFile = cache.get(fileBaseURI);

            if (lookingFile != null)
                return lookingFile;

        } catch (URISyntaxException e) {
            throw new DereferenceException(
                    "could not parse id field from file with uri: " + fileBaseURI.getCanonical());
        }

        return makeFile(fileBaseURI, sourceJson);
    }

    public File get(JsonNode sourceJson) throws DereferenceException{
        try {
            URI idFieldURI = getIdField(sourceJson);

            if (idFieldURI == null)
                throw new DereferenceException("anonymous schema should have field '$id'");

            BaseURI fileBaseURI = new BaseURI(idFieldURI, idFieldURI);
            File lookingFile = cache.get(fileBaseURI);

            if (lookingFile != null)
                return lookingFile;

            return makeFile(fileBaseURI, sourceJson);

        } catch (URISyntaxException e) {
            throw new DereferenceException(
                    "could not parse id field from file anonymous schema: ");
        }
    }

    private URI getIdField(JsonNode source) throws URISyntaxException {
        if (source.has("$id")) {
            return new URI(source.get("$id").toString());
        }

        return null;
    }

    private File makeFile(BaseURI baseURI, JsonNode sourceJson) throws DereferenceException {
        urnPool.updateCache(baseURI.getCanonical(), loaderFactory);
        File lookingFile = fileFactory.makeFile(this, baseURI.getCanonical(), sourceJson);
        cache.put(baseURI, lookingFile);
        lookingFile.dereference();

        return lookingFile;
    }
}
