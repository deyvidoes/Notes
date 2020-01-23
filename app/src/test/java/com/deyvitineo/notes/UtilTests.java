package com.deyvitineo.notes;

import com.deyvitineo.notes.util.Utility;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilTests {

    @Test
    public void canNoteBeSaved(){
        boolean result = Utility.canNoteBeSaved("Title", "Content");
        assertEquals(true, result);
    }

}
