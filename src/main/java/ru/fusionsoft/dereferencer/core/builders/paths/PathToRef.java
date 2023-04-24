package ru.fusionsoft.dereferencer.core.builders.paths;

import java.util.Objects;

public class PathToRef{
    private final String pathToRef;
    private final String refValue;
    private final int hash;

    public PathToRef(String pathToRef, String refValue){
        this.pathToRef = pathToRef;
        this.refValue = refValue;
        this.hash = Objects.hash(pathToRef, refValue);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if(!(obj instanceof PathToRef))
            return false;

        if(hash != obj.hashCode())
            return false;

        if(!pathToRef.equals(((PathToRef) obj).pathToRef))
            return false;
        if(!refValue.equals(((PathToRef) obj).refValue))
            return false;

        return true;
    }

    public String getPathToRef() {
        return pathToRef;
    }

    public String getRefValue() {
        return refValue;
    }

}
