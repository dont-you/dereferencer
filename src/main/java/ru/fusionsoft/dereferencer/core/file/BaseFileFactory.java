package ru.fusionsoft.dereferencer.core.file;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.Nullable;
import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.FileFactory;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.SourceTypeResolver;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class BaseFileFactory implements FileFactory {
    @Override
    public final File makeFile(FileRegister fileRegister, URI retrievalURI, InputStream stream, String mimetype) throws DereferenceException {
        try {
            JsonNode source = makeJsonFromStream(stream, mimetype);
            URI embeddedInContent = readURIEmbeddedInContent(source);
            URI baseURI = embeddedInContent != null ? retrievalURI.resolve(embeddedInContent) : retrievalURI;
            return makeFileInstance(fileRegister, baseURI, source);
        } catch (URISyntaxException e) {
            throw new DereferenceException("uri embedded in the content of file with retrieval uri - " + retrievalURI + " hava errors");
        } catch (IOException e) {
            throw new DereferenceException("could not read json from file with uri- " +  retrievalURI);
        }
    }

    protected JsonNode makeJsonFromStream(InputStream stream, String mimetype) throws IOException {
        return SourceTypeResolver.readJsonFrom(stream, mimetype);
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
