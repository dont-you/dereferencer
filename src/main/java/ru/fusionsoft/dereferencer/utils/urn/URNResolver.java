package ru.fusionsoft.dereferencer.utils.urn;

import java.net.URI;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.RetrievingException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;

public class URNResolver {
    Map<URN, URI> cache;

    public URNResolver() {
        cache = new HashMap<>();
    }

    static class URNCacheComporator implements Comparator<Entry<URN,URI>>{
        @Override
        public int compare(Entry<URN, URI> arg0, Entry<URN, URI> arg1) {
            return arg0.getKey().toString().length() - arg1.getKey().toString().length();
        }

    }

    public URI getLocator(URN urn) throws LoadException {
        String urnLiteral = urn.toString();
        URI uri = cache.entrySet().stream()
            .filter(e -> {
                String findUrn = e.getKey().toString();
                if(findUrn.endsWith("*"))
                    return urnLiteral.startsWith(findUrn.substring(0,findUrn.length()-1));
                else
                    return urnLiteral.equals(findUrn);
            })
            .max(new URNCacheComporator())
            .orElseThrow(() -> new URIException(String.format("urn %s is not defined",urnLiteral)))
            .getValue();

        if (urn.getNID().equals("tag")) {
            return TagUri.makeTargetUri(TagUri.parseByUrn(urn), uri);
        } else {

            throw new RetrievingException("uri with NID" + urn.getNID() + " not supported");
        }
    }

    public void addToCache(Map<URN, URI> map) {
        cache.putAll(map);
    }
}
