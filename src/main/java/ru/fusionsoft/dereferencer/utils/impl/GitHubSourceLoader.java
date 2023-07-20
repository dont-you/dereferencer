package ru.fusionsoft.dereferencer.utils.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;
import ru.fusionsoft.dereferencer.core.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.load.SupportedSourceTypes;

public class GitHubSourceLoader implements SourceLoader {

    private GitHub gitHub;
    private String repo;
    private String filePath;
    private String ref;

    public GitHubSourceLoader(String token, URI uri) throws LoadException {
        String parts[] = uri.getPath().split("/");
        repo = parts[1] + "/" + parts[2];
        ref = parts[4];
        filePath = parts[5];

        for (int i = 6; i < parts.length; i++) {
            filePath += "/" + parts[i];
        }

        setApiClient(token);
    }

    @Override
    public InputStream getSource() throws LoadException {
        try {
            return gitHub.getRepository(repo).getFileContent(filePath, ref).read();
        } catch (IOException e) {
            throw new UnknownException(String.format(
                    "error while getting file from github with: \n\trepo - %s\n\tfilepath - %s\n\tref - %s", repo,
                    filePath, ref));
        }
    }

    @Override
    public SupportedSourceTypes getSourceType() throws LoadException {
        String fileExtension = filePath.substring(filePath.lastIndexOf(".") + 1);
        return SupportedSourceTypes.resolveSourceTypeByMimeType("application/" + fileExtension);
    }

    private void setApiClient(String token) throws LoadException {
        try {
            gitHub = new GitHubBuilder().withOAuthToken(token).build();
        } catch (IOException e) {
            throw new UnknownException("could not create github api client");
        }
    }
}
