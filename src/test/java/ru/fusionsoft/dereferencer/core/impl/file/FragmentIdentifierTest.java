package ru.fusionsoft.dereferencer.core.impl.file;

import org.junit.Test;
import static org.junit.Assert.*;

public class FragmentIdentifierTest {
    @Test
    public void Test_constructor_When_given_pointerFragmentIdentifier(){
        assertEquals("/some/path",new FragmentIdentifier("/some/path").getPointer());
        assertNull(new FragmentIdentifier("/some/path").getPlainName());
    }

    @Test
    public void Test_constructor_When_given_emptyString_Or_null(){
        assertEquals("",new FragmentIdentifier("").getPointer());
        assertNull(new FragmentIdentifier("").getPlainName());

        assertEquals("",new FragmentIdentifier(null).getPointer());
        assertNull(new FragmentIdentifier(null).getPlainName());
    }

    @Test
    public void Test_constructor_When_given_plainNameFragmentIdentifier(){
        assertEquals("plain-name",new FragmentIdentifier("plain-name").getPlainName());
        assertNull(new FragmentIdentifier("plain-name").getPointer());

        assertEquals("some-other-name",new FragmentIdentifier("some-other-name").getPlainName());
        assertNull(new FragmentIdentifier("some-other-name").getPointer());
    }

    @Test
    public void Test_equals_method(){
        assertEquals(new FragmentIdentifier("/some/path"), new FragmentIdentifier("/some/path"));
        assertEquals(new FragmentIdentifier("plain-name"), new FragmentIdentifier("plain-name"));

        assertNotEquals(new FragmentIdentifier("plain-name"), new FragmentIdentifier("/some/path"));
        assertNotEquals(new FragmentIdentifier("/some/path"), new FragmentIdentifier("plain-name"));
    }

    @Test
    public void Test_isRelativePointer_method(){
        assertTrue(FragmentIdentifier.isRelativePointer("1/objects"));
        assertTrue(FragmentIdentifier.isRelativePointer("1#"));

        assertFalse(FragmentIdentifier.isRelativePointer("/some/path"));
        assertFalse(FragmentIdentifier.isRelativePointer("plain-name"));
    }
}
