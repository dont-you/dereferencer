package ru.fusionsoft.dereferencer.core.reference.impl.external;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.enums.ReferenceType;
import ru.fusionsoft.dereferencer.exception.ReferenceException;

public class GitHubReference extends URLReference {
    public GitHubReference(URI uri) {
        super(uri);
    }

    @Override
    public ReferenceType getReferenceType() {
        return ReferenceType.URL_GITHUB;
    }

    @Override
    public JsonNode getSource() throws ReferenceException {
        if (source == null) {
            try {
                HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
                conn.setRequestProperty("Accept", "application/vnd.github.v3.raw");
                if (Dereferencer.getGitHubToken() != null)
                    conn.setRequestProperty("Authorization", "token " + Dereferencer.getGitHubToken());

                if (getFileType().equals("yaml")) {
                    ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
                    Object obj = yamlMapper.readValue(conn.getInputStream(), Object.class);

                    source = Dereferencer.objectMapper.readTree(Dereferencer.objectMapper.writeValueAsString(obj));
                } else {
                    source = Dereferencer.objectMapper.readTree(conn.getInputStream());
                }

            } catch (IOException e) {
                throw new ReferenceException("error while getting json document from uri -{" + uri
                        + "} with message - \n" + e.getMessage());
            }
        }
        return source;
    }

    public String getFileType() {
        String filename = uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
