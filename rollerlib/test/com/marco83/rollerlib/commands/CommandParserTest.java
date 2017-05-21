/*
 * The MIT License
 *
 * Copyright 2017 marcoconti.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.marco83.rollerlib.commands;

import java.util.regex.Pattern;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author marcoconti
 */
public class CommandParserTest {
    
    public CommandParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    private static final Pattern ROLL_RESULT_PATTERN = Pattern.compile("Rolled\\.\\.\\. [0-9]+! \\(rolls: [0-9 ,]+\\)");

    @Test
    public void testParseRollValidDie() {
        
        // GIVEN
        CommandParser instance = new CommandParser();
        
        // WHEN
        String result = instance.parseText("/roll 3d6+3");
        
        // THEN
        assertTrue(result + " does not match", ROLL_RESULT_PATTERN.matcher(result).matches());
    }
    
    @Test
    public void testParseRollValidDieAdv() {
        
        // GIVEN
        CommandParser instance = new CommandParser();
        
        // WHEN
        String result = instance.parseText("/roll ADV 1d20+3");
        
        // THEN
        assertTrue(result + " does not match", ROLL_RESULT_PATTERN.matcher(result).matches());
    }
    
    @Test
    public void testParseInvalidRoll() {
        
        // GIVEN
        CommandParser instance = new CommandParser();
        
        // WHEN
        String result = instance.parseText("/roll a1d20+3");
        
        // THEN
        assertEquals(result, "ERROR: Unable to parse token 'a1d20'");
    }
    
    @Test
    public void testParseValidDieImplicitly() {
        // GIVEN
        CommandParser instance = new CommandParser();
        
        // WHEN
        String result = instance.parseText("3d6+3");
        
        // THEN
        assertTrue(result + " does not match", ROLL_RESULT_PATTERN.matcher(result).matches());
    }
    
        @Test
    public void testDoesNotParseInvalidDieImplicitly() {
        // GIVEN
        CommandParser instance = new CommandParser();
        
        // WHEN
        String result = instance.parseText("hello folks");
        
        // THEN
            assertNull(result);
    }
}
