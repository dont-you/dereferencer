package ru.fusionsoft.dereferencer.utils.urn;

import java.net.URI;
import java.net.URISyntaxException;

import ru.fusionsoft.dereferencer.core.exceptions.URIException;

public class TagUri {
    private final String authorityName;
    private final String date;
    private final String specific;
    private final String fragment;

    private TagUri(String authorityName, String date, String specific, String fragment) {
        this.authorityName = authorityName;
        this.date = date;
        this.specific = specific;
        this.fragment = fragment;
    }

    private static TagUri parse(String tagUri) {
        // TODO
        return null;
    }

    public static TagUri parse(URI uri) {
        return parse(uri.toASCIIString());
    }

    public static TagUri parseByUrn(URN urn) {
        String NSS = urn.getNSS();
        String[] parts = NSS.split(":");
        String taggingEntity = parts[0];

        int indexOfComma = taggingEntity.indexOf(",");
        String authorityName = taggingEntity.substring(0, indexOfComma);
        String date = taggingEntity.substring(indexOfComma + 1);

        String query = urn.getqComponent();
        if (query.length() > 0)
            query = "?" + query.substring(2);
        String specific = parts[1] + query;
        String fragment = urn.getfComponent();
        return new TagUri(authorityName, date, specific, fragment);
    }

    public static URI makeTargetUri(TagUri tagUri, URI locator) throws URIException {
        String uriLiteral = locator.toASCIIString() + tagUri.getFragment();
        try {
            return new URI(uriLiteral);
        } catch (URISyntaxException e) {
            throw new URIException("could not make uri by: "
                    + "\turi tag - " + tagUri
                    + "\tlocator - " + locator);
        }
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public String getDate() {
        return date;
    }

    public String getSpecific() {
        return specific;
    }

    public String getFragment() {
        return fragment;
    }

    public String getTaggingEntiry() {
        return authorityName + "," + date;
    }

    @Override
    public String toString() {
        return "tag:" + getTaggingEntiry() + ":" + specific + fragment;
    }
}
