package ru.fusionsoft.dereferencer.core.load;

import ru.fusionsoft.dereferencer.core.Resource;
import ru.fusionsoft.dereferencer.core.ResourceCenter;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.load.urn.URNResolver;

import java.io.InputStream;
import java.net.URI;

public class BaseResourceCenter implements ResourceCenter {
    private URLResourceLoader urlChain;
    private URNResolver urnResolver;

    public BaseResourceCenter(){
        urlChain=null;
        urnResolver=null;
    }

    @Override
    public Resource load(URI uri) throws DereferenceException {
        Resource resource = new Resource(uri);

        if(uri.getScheme()!=null && uri.getScheme().equals("urn"))
            resource.updateRetrieval(urnResolver.resolve(uri));

        urnResolver.updatePool(uri);
        urlChain.load(resource);
        return resource;
    }

    public InputStream loadOnlyStream(URI uri) throws DereferenceException {
        Resource resource = new Resource(uri);
        urlChain.load(resource);
        return resource.getStream();
    }

    public URLResourceLoader getURLChain() {
        return urlChain;
    }

    public BaseResourceCenter addURLLoader(URLResourceLoader urlResourceLoader){
        urlResourceLoader.setNextLoader(urlChain);
        this.urlChain = urlResourceLoader;
        return this;
    }

    public BaseResourceCenter clearURLChain(){
        urlChain.nextLoader = null;
        return this;
    }

    public BaseResourceCenter setURNResolver(URNResolver urnResolver){
        this.urnResolver = urnResolver;
        this.urnResolver.setBaseResourceCenter(this);
        return this;
    }

    public URNResolver getURNResolver(){
        return this.urnResolver;
    }
}
