package ru.fusionsoft.dereferencer;

import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.load.SchemaLoader;

public class Dereferencer{
    private SchemaLoader schemaLoader;

    public Dereferencer(){
        // TODO
    }

    public Dereferencer(DereferenceConfiguration cfg){
        // TODO
    }

    public static JsonNode dereference(URI uri){
        // TODO
        return null;
    }

    public static JsonNode dereference(URI uri, DereferenceConfiguration cfg){
        // TODO
        return null;
    }

    public static JsonNode dereference(String uri){
        // TODO
        return null;
    }

    public static JsonNode dereference(String uri, DereferenceConfiguration cfg){
        // TODO
        return null;
    }

    public static JsonNode anonymousDereference(JsonNode node){
        // TODO
        return null;
    }

    public static JsonNode anonymousDereference(JsonNode node, DereferenceConfiguration cfg){
        // TODO
        return null;
    }

    public JsonNode deref(URI uri){
        // TODO
        return null;
    }
    public JsonNode deref(String uri){
        // TODO
        return null;
    }

    public void setDereferenceConfiguration(){
        // TODO
    }
}
