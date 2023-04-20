package ru.fusionsoft.dereferencer.enums;

import java.net.URI;

public enum ReferenceType{
    LOCAL,
    REMOTE,
    URL;

    public static boolean isURLReference(URI uri){
        if(uri.getHost()!=null)
            return true;

        return false;
    }


    public static boolean isRemoteReference(URI uri){
        if(uri.getHost()==null && !uri.getPath().equals(""))
            return true;
        return false;
    }


    public static boolean isLocalReference(URI uri){
        if(uri.getPath().equals("") && uri.getFragment()!=null)
            return true;

        return false;
    }
}