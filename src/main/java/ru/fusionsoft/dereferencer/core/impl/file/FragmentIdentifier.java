package ru.fusionsoft.dereferencer.core.impl.file;

import org.apache.commons.lang3.StringUtils;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.util.Objects;

public class FragmentIdentifier {
    private String pointer;
    private String plainName;
    private boolean endsWithHash;

    public FragmentIdentifier(String fragment) {
        if (fragment == null || fragment.equals(""))
            pointer = "";
        else if (fragment.startsWith("/"))
            pointer = fragment;
        else
            plainName = fragment;
    }

    private FragmentIdentifier(String pointer, String plainName, boolean endsWithHash) {
        this.pointer = pointer;
        this.plainName = plainName;
        this.endsWithHash = endsWithHash;
    }

    public String getPointer() {
        return pointer;
    }

    public String getPlainName() {
        return plainName;
    }

    public String getPropertyName() throws DereferenceException{
        return getPropertyName(pointer);
    }

    public static String getPropertyName(String pointer) throws DereferenceException{
        if(pointer.equals(""))
            throw new DereferenceException("pointer '' no have property name");

        return pointer.substring(pointer.lastIndexOf("/") + 1);
    }

    public FragmentIdentifier getParentPtr() throws DereferenceException{
        return new FragmentIdentifier(getParentPointer(pointer));
    }

    public static String getParentPointer(String pointer) throws DereferenceException{
        if(pointer.equals(""))
            throw new DereferenceException("pointer '' no have parent pointer");

        return pointer.substring(0, pointer.lastIndexOf("/"));
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;

        FragmentIdentifier checkPtr = (FragmentIdentifier) obj;

        if (plainName != null && checkPtr.getPlainName() != null)
            return plainName.equals(checkPtr.plainName);

        if (pointer != null && checkPtr.getPointer() != null)
            return pointer.equals(checkPtr.pointer);

        return false;
    }

    public static boolean isRelativePointer(String pointer) {
        if (pointer.length() == 0)
            return false;

        char c = pointer.charAt(0);
        return c >= '0' && c <= '9';
    }

    public static FragmentIdentifier resolveRelativePtr(String pathToRef, String pointer) {
        StringBuilder currentPath = new StringBuilder(pathToRef);
        boolean endsWithHash;

        if (pointer.endsWith("#")) {
            pointer = pointer.substring(0, pointer.length() - 1);
            endsWithHash = true;
        } else {
            endsWithHash = false;
        }

        String[] pointerParts = pointer.split("/");
        boolean isJsonPointer = false;

        for (String key : pointerParts) {
            if (StringUtils.isNumeric(key) && !isJsonPointer) {
                int upLevelTo = Integer.parseInt(key);
                for (int i = 0; i < upLevelTo; i++) {
                    currentPath = new StringBuilder(currentPath.substring(0, currentPath.lastIndexOf("/")));
                }
            } else {
                currentPath.append("/").append(key);
                isJsonPointer = true;
            }
        }

        return new FragmentIdentifier(currentPath.toString(), null, endsWithHash);
    }

    public boolean isAnchorPointer() {
        return plainName != null;
    }

    public boolean endsWithHash() {
        return endsWithHash;
    }
}
