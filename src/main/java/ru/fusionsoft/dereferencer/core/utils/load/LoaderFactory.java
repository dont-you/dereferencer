package ru.fusionsoft.dereferencer.core.utils.load;

import java.net.URI;

import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.utils.load.impl.FileLoader;
import ru.fusionsoft.dereferencer.core.utils.load.impl.GitHubLoader;
import ru.fusionsoft.dereferencer.core.utils.load.impl.URLLoader;

public class LoaderFactory{
    private FileLoader fileLoader;
    private GitHubLoader gitHubLoader;
    private URLLoader urlLoader;

    public LoaderFactory(){
        fileLoader = new FileLoader();
        gitHubLoader = new GitHubLoader();
        urlLoader = new URLLoader();
    }

    public SourceLoader getLoader(URI uri) throws URIException{
        if(isGitHubReference(uri))
            return gitHubLoader;
        else if (isFileSystemReference(uri))
            return fileLoader;
        else if (isURLReference(uri))
            return urlLoader;
        else
            // TODO
            throw new URIException("");
    }

    public void setGitHubToken(String token){
        gitHubLoader.setToken(token);
    }

    public void setGitLabToken(String token){
        // TODO ...add gitlab loader
    }

    private boolean isGitHubReference(URI uri) {
        return uri.getHost() != null
                && uri.getHost().equals(Dereferencer.PROPERTIES.getProperty("refs.hostname.github"));
    }

    private boolean isURLReference(URI uri) {
        return uri.getHost() != null;
    }

    private boolean isFileSystemReference(URI uri) {
        return uri.getHost() == null && !uri.getPath().equals("");
    }

}
