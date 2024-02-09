package ru.fusionsoft.dereferencer.git;

import ru.fusionsoft.dereferencer.core.LoaderFactory;
import ru.fusionsoft.dereferencer.core.SourceLoader;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.impl.load.BaseLoaderFactory;

import java.io.IOException;
import java.net.URI;

public class GitLoaderFactory implements LoaderFactory {
    private final BaseLoaderFactory baseLoaderFactory;
    private final GitHubLoader gitHubLoader;
    private final GitLabLoader gitLabLoader;

    public GitLoaderFactory() throws DereferenceException {
        this.baseLoaderFactory = new BaseLoaderFactory();
        try {
            this.gitHubLoader = new GitHubLoader();
        } catch (IOException e) {
            throw new DereferenceException("error while configuring github loader", e);
        }
        this.gitLabLoader = new GitLabLoader();
    }

    public GitHubLoader getGitHubLoader() {
        return gitHubLoader;
    }

    public GitLabLoader getGitLabLoader() {
        return gitLabLoader;
    }

    @Override
    public SourceLoader getSourceLoader(URI uri) throws DereferenceException {
        if (gitHubLoader.canLoad(uri))
            return gitHubLoader;
        else if (gitLabLoader.canLoad(uri))
            return gitLabLoader;
        else
            return baseLoaderFactory.getSourceLoader(uri);
    }
}
