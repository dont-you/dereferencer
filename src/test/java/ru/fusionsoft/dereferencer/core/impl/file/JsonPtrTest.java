package ru.fusionsoft.dereferencer.core.impl.file;

import org.junit.Test;
import ru.fusionsoft.dereferencer.core.impl.file.JsonPtr;

import static org.junit.Assert.*;

public class JsonPtrTest {
    @Test
    public void Test_constructor_By_fragment(){
        String pathRef = "/some/path/to/node";
        JsonPtr pathPtr = new JsonPtr(pathRef);
        assertEquals(pathRef,pathPtr.getPointer());
        assertNull(pathPtr.getPlainName());

        String pathRef2 = "";
        JsonPtr pathPtr2 = new JsonPtr(pathRef2);
        assertEquals(pathRef2,pathPtr2.getPointer());
        assertNull(pathPtr2.getPlainName());

        String anchorRef = "someanchor";
        JsonPtr anchorPtr = new JsonPtr(anchorRef);
        assertEquals(anchorRef,anchorPtr.getPlainName());
        assertNull(anchorPtr.getPointer());
    }

    @Test
    public void Test_method_makeRedirectedPointer(){
        JsonPtr targetPtr = new JsonPtr("/some/path/via/reference");
        JsonPtr gatewayPtr = new JsonPtr("/some/path");
        String expectedResult = "/via/reference";

        assertEquals(expectedResult ,targetPtr.makeRedirectedPointer(gatewayPtr).getPointer());
    }

    @Test
    public void Test_method_getPropertyName(){
        JsonPtr targetPtr = new JsonPtr("/some/property");
        String expectedResult = "property";

        assertEquals(expectedResult , targetPtr.getPropertyName());
    }

    @Test
    public void Test_method_getPropertyName_With_empty_path(){
        JsonPtr targetPtr = new JsonPtr("");
        String expectedResult = "";

        assertEquals(expectedResult , targetPtr.getPropertyName());
    }

    @Test
    public void Test_method_getParentPtr(){
        JsonPtr targetPtr = new JsonPtr("/some/path");
        String expectedResult1 = "/some";
        String expectedResult2 = "";

        assertEquals(expectedResult1 , targetPtr.getParentPtr().getPointer());
        assertEquals(expectedResult2 , targetPtr.getParentPtr().getParentPtr().getPointer());
    }

    @Test
    public void Test_method_isSupSetTo(){
        JsonPtr targetPtr = new JsonPtr("/some/path");
        JsonPtr sub = new JsonPtr("/some/path/1/2/3");
        JsonPtr notSub = new JsonPtr("/another/path");

        assertTrue(targetPtr.isSupSetTo(sub));
        assertFalse(targetPtr.isSupSetTo(notSub));
    }
}