package ru.fusionsoft.dereferencer.utils.urn;

import java.net.URI;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;

public class URN{
    private String NID;
    private String NSS;
    private String rqComponent;
    private String fComponent;

    public URN(URI uri) throws LoadException{
    }

    public String getNID() {
        return NID;
    }

    public String getNSS() {
        return NSS;
    }

    public String getRqComponent() {
        return rqComponent;
    }

    public String getfComponent() {
        return fComponent;
    }

    @Override
    public String toString(){
        return "urn:" + NID + ":" + NSS + rqComponent + fComponent;
    }

    @Override
    public boolean equals(Object obj) {
        URN urn = (URN) obj;
        return NID.equals(urn.getNID()) && NSS.equals(urn.getNSS());
    }
}
