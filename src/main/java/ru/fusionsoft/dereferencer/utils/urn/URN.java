package ru.fusionsoft.dereferencer.utils.urn;

import java.net.URI;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;

public class URN{
    private final String NID;
    private final String NSS;
    private final String rqComponent;
    private final String fComponent;

    private URN(String NID, String NSS, String rqComponent,String fComponent){
        this.NID = NID;
        this.NSS = NSS;
        this.rqComponent = rqComponent;
        this.fComponent = fComponent;
    }

    private static URN parse(String urn) throws LoadException{
        String[] parts = urn.toString().split(":");

        if(parts.length<3)
            throw new URIException(String.format("wrong syntax of urn %s",urn));

        String NID = parts[1];
        String schemeSpecificPart = urn.substring(parts[1].length() + 5);
        String NSS = substringUntilAny(schemeSpecificPart, "?=", "?+", "#");
        String rqfComponents = schemeSpecificPart.substring(NSS.length());

        int fCompIndex = rqfComponents.indexOf("#");
        String rqComponent = rqfComponents.substring(0,fCompIndex);
        String fComponent = rqComponent.substring(fCompIndex);

        return new URN(NID, NSS, rqComponent, fComponent);
    }

    private static String substringUntilAny(String line, String... endDelimiters){
        int endIndex = line.length();
        for(String delimiter: endDelimiters){
            if(line.contains(delimiter)){
                int index = line.indexOf(delimiter);
                endIndex = endIndex > index ? index : endIndex;
            }

        }

        return line.substring(0, endIndex);
    }

    public static URN parse(URI uri) throws LoadException{
        return parse(uri.toASCIIString());
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
