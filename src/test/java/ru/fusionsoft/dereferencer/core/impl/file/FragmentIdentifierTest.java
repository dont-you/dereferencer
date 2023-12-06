package ru.fusionsoft.dereferencer.core.impl.file;

import org.junit.Test;

import static org.junit.Assert.*;

public class FragmentIdentifierTest {
    @Test
    public void Test_constructor_By_fragment(){
        String pathRef = "/some/path/to/node";
        FragmentIdentifier pathPtr = new FragmentIdentifier(pathRef);
        assertEquals(pathRef,pathPtr.getPointer());
        assertNull(pathPtr.getPlainName());

        String pathRef2 = "";
        FragmentIdentifier pathPtr2 = new FragmentIdentifier(pathRef2);
        assertEquals(pathRef2,pathPtr2.getPointer());
        assertNull(pathPtr2.getPlainName());

        String anchorRef = "someanchor";
        FragmentIdentifier anchorPtr = new FragmentIdentifier(anchorRef);
        assertEquals(anchorRef,anchorPtr.getPlainName());
        assertNull(anchorPtr.getPointer());
    }

    @Test
    public void Test_method_makeRedirectedPointer(){
        FragmentIdentifier targetPtr = new FragmentIdentifier("/some/path/via/reference");
        FragmentIdentifier gatewayPtr = new FragmentIdentifier("/some/path");
        String expectedResult = "/via/reference";

//        assertEquals(expectedResult ,targetPtr.makeRedirectedPointer(gatewayPtr).getPointer());
    }

    @Test
    public void Test_method_getPropertyName(){
        FragmentIdentifier targetPtr = new FragmentIdentifier("/some/property");
        String expectedResult = "property";

        assertEquals(expectedResult , targetPtr.getPropertyName());
    }

    @Test
    public void Test_method_getPropertyName_With_empty_path(){
        FragmentIdentifier targetPtr = new FragmentIdentifier("");
        String expectedResult = "";

        assertEquals(expectedResult , targetPtr.getPropertyName());
    }

    @Test
    public void Test_method_getParentPtr(){
        FragmentIdentifier targetPtr = new FragmentIdentifier("/some/path");
        String expectedResult1 = "/some";
        String expectedResult2 = "";

        assertEquals(expectedResult1 , targetPtr.getParentPtr().getPointer());
        assertEquals(expectedResult2 , targetPtr.getParentPtr().getParentPtr().getPointer());
    }

    @Test
    public void Test_method_isSupSetTo(){
        FragmentIdentifier targetPtr = new FragmentIdentifier("/some/path");
        FragmentIdentifier sub = new FragmentIdentifier("/some/path/1/2/3");
        FragmentIdentifier notSub = new FragmentIdentifier("/another/path");

        assertTrue(targetPtr.isSupSetTo(sub));
        assertFalse(targetPtr.isSupSetTo(notSub));
    }
}