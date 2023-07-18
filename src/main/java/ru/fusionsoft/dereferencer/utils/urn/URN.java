package ru.fusionsoft.dereferencer.utils.urn;

import java.net.URI;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;

public class URN implements Comparable{
    private final String NID;
    private final String NSS;
    private final String rComponent;
    private final String qComponent;
    private final String fComponent;

    private URN(String NID, String NSS, String rComponent, String qComponent, String fComponent) {
        this.NID = NID;
        this.NSS = NSS;
        this.rComponent = rComponent;
        this.qComponent = qComponent;
        this.fComponent = fComponent;
    }

    private static URN parse(String urn) throws LoadException {
        String[] parts = urn.toString().split(":");

        if (parts.length < 3)
            throw new URIException(String.format("wrong syntax of urn %s", urn));

        String NID = parts[1];
        String schemeSpecificPart = urn.substring(parts[1].length() + 5);
        String NSS = substringUntilAny(schemeSpecificPart, "?=", "?+", "#");
        String rqfComponents = schemeSpecificPart.substring(NSS.length());

        String rComponent = substringUntilAny(rqfComponents, "?=", "#");
        String qComponent = substringUntilAny(rqfComponents.substring(rComponent.length()), "#");
        String fComponent = rqfComponents.substring(rComponent.length() + qComponent.length());

        return new URN(NID, NSS, rComponent, qComponent, fComponent);
    }

    private static String substringUntilAny(String line, String... endDelimiters) {
        int endIndex = line.length();
        for (String delimiter : endDelimiters) {
            if (line.contains(delimiter)) {
                int index = line.indexOf(delimiter);
                endIndex = endIndex > index ? index : endIndex;
            }

        }

        return line.substring(0, endIndex);
    }

    public static URN parse(URI uri) throws LoadException {
        return parse(uri.toASCIIString());
    }

    public String getNID() {
        return NID;
    }

    public String getNSS() {
        return NSS;
    }

    public String getfComponent() {
        return fComponent;
    }

    public String getrComponent() {
        return rComponent;
    }

    public String getqComponent() {
        return qComponent;
    }

    @Override
    public String toString() {
        return "urn:" + NID + ":" + NSS + rComponent + qComponent + fComponent;
    }

    @Override
    public boolean equals(Object obj) {
        URN urn = (URN) obj;
        return NID.equals(urn.getNID()) && NSS.equals(urn.getNSS());
    }

    @Override
    public int compareTo(Object arg0) {
        return 0;
    }
}
