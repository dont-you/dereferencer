package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.tika.Tika;
import ru.fusionsoft.dereferencer.core.load.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class DereferencedFileFactory {

    protected static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    protected static final ObjectMapper jsonMapper = new ObjectMapper();
    protected static final Tika tika = new Tika();

    public final DereferencedFile makeFile(Resource resource) throws IOException{
        JsonNode source = readJsonFromInputStream(resource.getInputStream(), resource.getRetrievalURI());
        URI baseURI = establishBaseURI(resource.getRetrievalURI(), source);
        return makeInstance(baseURI, source);
    }

    protected JsonNode readJsonFromInputStream(InputStream inputStream, URI retrievalURI) throws IOException {
        String mimetype = tika.detect(retrievalURI.toURL());

        if(mimetype.contains("json") || mimetype.contains("schema+json"))
            return jsonMapper.readTree(inputStream);
        else if (mimetype.contains("x-yaml") || mimetype.contains("yaml"))
            return yamlMapper.readTree(inputStream);
        else
            throw new RuntimeException("mimetype " + mimetype + " is not supported");

    }

    protected URI establishBaseURI(URI retrievalURI, JsonNode source){
        if(source.has("$id"))
            return retrievalURI.resolve(source.get("$id").asText());
        else
            return retrievalURI;
    }

    protected DereferencedFile makeInstance(URI baseURI, JsonNode source){
        return new FileImpl(baseURI,source);
    }
}
