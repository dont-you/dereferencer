package ru.fusionsoft.dereferencer.core.impl.urn;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class TagURI implements Comparable<TagURI>{
    private final String taggingEntity;
    private final String[] nameSpaceSpecificParts;

    public TagURI(String taggingEntity, String specific){
        this.taggingEntity = taggingEntity;
        this.nameSpaceSpecificParts = specific.split("\\.");
    }

    public static TagURI parse(URI tagUri){
        String[] parts = tagUri.getSchemeSpecificPart().split(":",2);
        return new TagURI(parts[0], parts[1]);
    }

    public static boolean isSub(TagURI subTagUri,TagURI supTagUri){
        if(!subTagUri.taggingEntity.equals(supTagUri.taggingEntity))
            return false;

        String[] supParts = supTagUri.nameSpaceSpecificParts;
        String[] subParts = subTagUri.nameSpaceSpecificParts;

        if(supParts.length > subParts.length)
            return false;

        for (int i = 0; i < supParts.length;i++){
            if(!supParts[i].equals(subParts[i]) && !supParts[i].equals("*"))
                return false;
        }
        return true;
    }

    public static URI resolve(TagURI target, URI locator) throws URISyntaxException{
        String removablePart = String.join("/",target.nameSpaceSpecificParts).concat(".yaml");
        StringBuilder addablePart = new StringBuilder();
        boolean locatorContainsRemovablePart = false;

        while(!locatorContainsRemovablePart){
            if(locator.toString().endsWith(removablePart)){
                locatorContainsRemovablePart = true;
            } else {
                String add = removablePart.substring(removablePart.lastIndexOf("/")+1);
                removablePart = removablePart.substring(0,removablePart.lastIndexOf("/")+1);
                addablePart.insert(0, add + "/");
            }
        }

        return locator.resolve(addablePart.deleteCharAt(addablePart.lastIndexOf("/")).toString());
    }

    public int getPrefixSize(){
        return nameSpaceSpecificParts.length;
    }

    @Override
    public int compareTo(TagURI tagURI) {
        int tagEntCompare = taggingEntity.compareTo(tagURI.taggingEntity);
        return tagEntCompare!=0?tagEntCompare:Arrays.compare(nameSpaceSpecificParts, tagURI.nameSpaceSpecificParts);
    }
}
