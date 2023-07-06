package ru.fusionsoft.dereferencer.core.routing.ref;

import java.util.Objects;

public class JsonPtr {
    private String jsonPointer = null;
    private String plainName = null;
    private JsonPtr parentJsonPointer = null;
    private String propertyName = null;

    public JsonPtr(String fragment) {
        if (fragment.startsWith("/") || fragment.equals(""))
            jsonPointer = fragment;
        else
            plainName = fragment;
    }

    public JsonPtr(String jsonPointer, String plainName) {
        this.jsonPointer = jsonPointer;
        this.plainName = plainName;
    }

    public boolean isResolved() {
        return jsonPointer != null;
    }

    public String getResolved() {
        return jsonPointer;
    }

    public String getPlainName() {
        return plainName;
    }

    public JsonPtr getParent() {
        return Objects.requireNonNullElseGet(parentJsonPointer, () -> parentJsonPointer = new JsonPtr(jsonPointer.substring(0, jsonPointer.lastIndexOf("/"))));
    }

    public String getPropertyName() {
        if (parentJsonPointer == null)
            return propertyName = propertyName.substring(propertyName.lastIndexOf("/") + 1);
        else
            return propertyName;
    }

    public JsonPtr subtractPtr(JsonPtr ptr) {
        return new JsonPtr(ptr.getResolved().replace(jsonPointer, ""));
    }

    public boolean isSuperSetTo(JsonPtr ptr) {
        if (ptr.isResolved() && this.isResolved() && ptr.getResolved().length() != this.getResolved().length())
            return ptr.getResolved().startsWith(this.getResolved());
        else
            return false;
    }

    @Override
    public boolean equals(Object obj) {
        if(getClass() != obj.getClass())
            return false;

        JsonPtr rightPtr = (JsonPtr) obj;
        if (rightPtr.isResolved() && this.isResolved()) {
            return this.jsonPointer == rightPtr.jsonPointer;
        } else {
            return this.plainName == rightPtr.plainName;
        }
    }
}
