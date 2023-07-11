package ru.fusionsoft.dereferencer.utils.urn;

import java.net.URI;

public class TagUri{
    private String authorityName;
    private String date;
    private String specific;
    private String fragment;

    public TagUri(URI uri){
    }

    public static URI makeTargetUri(TagUri tagUri, URI uri){
        return null;
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

    public String getTaggingEntiry(){
        return authorityName + "," + date;
    }

    @Override
    public String toString(){
        return "tag:" + getTaggingEntiry() + ":" + specific + fragment;
    }
}
