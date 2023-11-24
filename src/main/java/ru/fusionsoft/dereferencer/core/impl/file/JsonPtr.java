package ru.fusionsoft.dereferencer.core.impl.file;

import java.util.Objects;

public class JsonPtr {
    private String pointer;
    private String plainName;
    private String propertyName;
    private JsonPtr parentPtr;

    public JsonPtr(String fragment){
        if (fragment.startsWith("/") || fragment.equals(""))
            pointer = fragment;
        else
            plainName = fragment;
    }

    public JsonPtr(String pointer, String plainName){
        this.pointer = pointer;
        this.plainName = plainName;
    }

    public JsonPtr makeRedirectedPointer(JsonPtr gateway){
        return new JsonPtr(pointer.replaceFirst(gateway.pointer, ""));
    }

    public String getPointer(){
        return pointer;
    }

    public String getPlainName(){
        return plainName;
    }

    public String getPropertyName(){
        return Objects.requireNonNullElseGet(propertyName,
                () -> propertyName = pointer.substring(pointer.lastIndexOf("/") + 1));
    }

    public JsonPtr getParentPtr() {
        return Objects.requireNonNullElseGet(parentPtr,
                () -> parentPtr = new JsonPtr(pointer.substring(0, pointer.lastIndexOf("/"))));
    }

    public boolean isSupSetTo(JsonPtr subPointer){
        if(pointer==null)
            return false;

        return subPointer.getPointer().startsWith(pointer);
    }

    @Override
    public boolean equals(Object obj){
        if(getClass() != obj.getClass())
            return false;

        JsonPtr checkPtr = (JsonPtr) obj;

        if(plainName!= null && checkPtr.getPlainName()!=null)
            return plainName.equals(checkPtr.plainName);

        if(pointer!= null && checkPtr.getPointer()!=null)
            return pointer.equals(checkPtr.pointer);

        return false;
    }

    public static boolean isRelativePointer(String pointer){
        if (pointer.length() == 0)
            return false;

        char c = pointer.charAt(0);
        return c >= '0' && c <= '9';
    }

    public static JsonPtr resolveRelativePtr(String pathToRef, String pointer){
        String currentPath = pathToRef;
        String[] pointerParts = pointer.split("/");
        for(String key: pointerParts){
            try{
                int upLevelTo = Integer.parseInt(key);
                for(int i=0 ; i < upLevelTo ; i++){
                    currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
                }
            } catch (NumberFormatException e) {
                currentPath += "/" + key;
            }
        }

        return new JsonPtr(currentPath);
    }

    public boolean isAnchorPointer(){
        return plainName != null;
    }
}
