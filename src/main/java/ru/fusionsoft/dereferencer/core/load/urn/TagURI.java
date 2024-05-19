package ru.fusionsoft.dereferencer.core.load.urn;

import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Arrays;

public class TagURI implements Comparable<TagURI> {
    private final String taggingEntity;
    private final String[] nameSpaceSpecificParts;

    public TagURI(String taggingEntity, String specific) {
        this.taggingEntity = taggingEntity;
        this.nameSpaceSpecificParts = specific.split("\\.");
    }

    public static TagURI parse(URI tagUri) {
        String[] parts = tagUri.getSchemeSpecificPart().split(":", 2);
        return new TagURI(parts[0], parts[1]);
    }

    public static URI resolve(URI locator, String dynamicPartOfLocator) {
        return Paths.get(locator.getPath()).resolveSibling(dynamicPartOfLocator).toUri();
    }

    public @Nullable String getSubPart(TagURI supTagURI) {
        if (supTagURI.nameSpaceSpecificParts[supTagURI.nameSpaceSpecificParts.length - 1].equals("*"))
            return String.join("/", Arrays.copyOfRange(nameSpaceSpecificParts, supTagURI.nameSpaceSpecificParts.length - 1, nameSpaceSpecificParts.length)).concat(".yaml");
        else
            return "";
    }

    public boolean isSup(TagURI subTagURI) {
        if (!taggingEntity.equals(subTagURI.taggingEntity)
                || subTagURI.nameSpaceSpecificParts.length < nameSpaceSpecificParts.length)
            return false;

        for (int i = 0; i < nameSpaceSpecificParts.length; i++) {
            if (!nameSpaceSpecificParts[i].equals(subTagURI.nameSpaceSpecificParts[i])
                    && !nameSpaceSpecificParts[i].equals("*"))
                return false;
        }

        return true;
    }

    @Override
    public int compareTo(TagURI tagURI) {
        int tagEntCompare = taggingEntity.compareTo(tagURI.taggingEntity);
        return tagEntCompare != 0 ? tagEntCompare
                : Arrays.compare(nameSpaceSpecificParts, tagURI.nameSpaceSpecificParts);
    }
}
