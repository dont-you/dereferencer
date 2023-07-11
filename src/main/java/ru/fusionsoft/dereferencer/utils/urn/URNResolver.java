package ru.fusionsoft.dereferencer.utils.urn;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.RetrievingException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;

public class URNResolver{
    Map<URN, URI> cache;

    public URNResolver(){
        cache = new HashMap<>();
    }

    public URI getLocator(URN urn) throws LoadException{
        URI uri = cache.get(urn);

        if(urn.getNID().equals("tag")){
            try {
                return TagUri.makeTargetUri(new TagUri(new URI(urn.getNID() + urn.getNSS() + urn.getRqComponent() + urn.getfComponent())),
                                     uri);
            } catch (URISyntaxException e) {
                throw new URIException(""); // TODO
            }

        }else{

                throw new RetrievingException("uri with NID" + urn.getNID() + " not supported");
        }
    }

    public void addToCache(Map<URN, URI> map){
        cache.putAll(map);
    }
}
