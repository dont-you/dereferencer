package ru.fusionsoft.dereferencer.core.impl.file;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.Nullable;
import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.FileFactory;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;
import java.net.URISyntaxException;

public class BaseFileFactory implements FileFactory {
    @Override
    public final File makeFile(FileRegister fileRegister, URI retrievalURI, JsonNode source) throws DereferenceException {
        try {
            URI embeddedInContent = readURIEmbeddedInContent(source);
            URI baseURI = embeddedInContent != null ? retrievalURI.resolve(embeddedInContent) : retrievalURI;
            return makeFileInstance(fileRegister, baseURI, source);
        } catch (URISyntaxException e) {
            throw new DereferenceException("uri embedded in the content of file with retrieval uri - " + retrievalURI + " hava errors");
        }
    }

    protected @Nullable URI readURIEmbeddedInContent(JsonNode content) throws URISyntaxException {
        if (content.has("$id")) {
            return new URI(content.get("$id").asText());
        }

        return null;
    }

    protected File makeFileInstance(FileRegister fileRegister, URI baseURI, JsonNode source) {
        return new BaseFile(fileRegister, baseURI, source);
    }
}
