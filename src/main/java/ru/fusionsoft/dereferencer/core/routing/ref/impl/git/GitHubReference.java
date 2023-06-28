package ru.fusionsoft.dereferencer.core.routing.ref.impl.git;

import java.net.URI;

import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.utils.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.utils.load.impl.GitHubLoader;

public class GitHubReference extends Reference {
    public GitHubReference(URI uri) throws URIException {
        super(uri);
    }

    public SourceLoader getSourceLoader(){
        return GitHubLoader.getInstance();
    }
}
