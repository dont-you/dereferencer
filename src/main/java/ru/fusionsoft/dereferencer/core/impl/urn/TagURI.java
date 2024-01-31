package ru.fusionsoft.dereferencer.core.impl.urn;

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

    public @Nullable String getSubPart(TagURI supTagURI) {
        int isSub = isSub(supTagURI);

        if (isSub < 0)
            return null;
        else if (isSub > 0)
            return String.join("/", Arrays.copyOfRange(nameSpaceSpecificParts, supTagURI.nameSpaceSpecificParts.length - 1, nameSpaceSpecificParts.length)).concat(".yaml");
        else
            return "";
    }

    public int isSub(TagURI supTagURI) {
        if (!supTagURI.taggingEntity.equals(taggingEntity)
                || nameSpaceSpecificParts.length < supTagURI.nameSpaceSpecificParts.length)
            return -1;

        for (int i = 0; i < supTagURI.nameSpaceSpecificParts.length; i++) {
            if (!nameSpaceSpecificParts[i].equals(supTagURI.nameSpaceSpecificParts[i])
                    && !supTagURI.nameSpaceSpecificParts[i].equals("*"))
                return -1;
        }

        if (supTagURI.nameSpaceSpecificParts[supTagURI.nameSpaceSpecificParts.length - 1].equals("*"))
            return 1;

        return 0;
    }

    public static URI resolve(URI locator, String dynamicPartOfLocator) {
        return Paths.get(locator.getPath()).resolveSibling(dynamicPartOfLocator).toUri();
    }

    @Override
    public int compareTo(TagURI tagURI) {
        int tagEntCompare = taggingEntity.compareTo(tagURI.taggingEntity);
        return tagEntCompare != 0 ? tagEntCompare
                : Arrays.compare(nameSpaceSpecificParts, tagURI.nameSpaceSpecificParts);
    }
}
