package ru.fusionsoft.dereferencer;

import java.io.IOException;
import java.net.URISyntaxException;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.fusionsoft.dereferencer.exception.ReferenceException;

public class App
{
    static ObjectMapper objectMapper = new ObjectMapper();

    public static void main( String[] args ) throws URISyntaxException, StreamReadException, DatabindException, IOException, ReferenceException
    {
        System.out.println(Dereferencer.dereference("/home/who/js.json"));
    }
}
