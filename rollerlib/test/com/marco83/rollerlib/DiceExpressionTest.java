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
package com.marco83.rollerlib;

import com.marco83.rollerlib.dice.DiceExpression;
import com.marco83.rollerlib.dice.RollResult;
import com.marco83.rollerlib.dice.parsing.DiceExpressionParser;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author marcoconti
 */
public class DiceExpressionTest {
    
    public DiceExpressionTest() {
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
    
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testExactNumber() {
        // GIVEN
        DiceExpression sut = DiceExpressionParser.parse("20");
        
        // THEN
        assertEquals(sut.toString(), "+20");
        assertEquals(sut.roll()[0].total, 20);
    }
    
    @Test
    public void testInvalidExpressionEndingPlus() {
        exception.expect(IllegalArgumentException.class);
        DiceExpressionParser.parse("10+");        
    }
    
    @Test
    public void testInvalidExpressionSpaces() {
        exception.expect(IllegalArgumentException.class);
        DiceExpressionParser.parse("1 0");        
    }
    
    @Test
    public void testInvalidExpressionEndingD() {
        exception.expect(IllegalArgumentException.class);
        DiceExpressionParser.parse("10d");        
    }

    @Test
    public void testInvalidExpressionText() {
        exception.expect(IllegalArgumentException.class);
        DiceExpressionParser.parse("Foo1d4");        
    }
    
    @Test
    public void testInvalidExpressionRepeatedPlus() {
        exception.expect(IllegalArgumentException.class);
        DiceExpressionParser.parse("1d4++4");        
    }
    
    @Test
    public void testExactNegativeNumber() {
        // GIVEN
        DiceExpression sut = DiceExpressionParser.parse("-20");
        
        // THEN
        assertEquals(sut.toString(), "-20");
        assertEquals(sut.roll()[0].total, -20);
    }
    
    @Test
    public void testSumOfNumbers() {
        // GIVEN
        DiceExpression sut = DiceExpressionParser.parse("-20+10+5-0-5+20");
        
        // THEN
        assertEquals(sut.toString(), "+10");
        assertEquals(sut.roll()[0].total, 10);
    }
    
    @Test
    public void testNegativeSumOfNumbers() {
        // GIVEN
        DiceExpression sut = DiceExpressionParser.parse("0+10+5-5-20-0");
        
        // THEN
        assertEquals(sut.toString(), "-10");
        assertEquals(sut.roll()[0].total, -10);
    }
    
    @Test
    public void testNegativeSumOfNumbersSpaces() {
        // GIVEN
        DiceExpression sut = DiceExpressionParser.parse("0+ 10+5- 5 -20 -  0");
        
        // THEN
        assertEquals(sut.toString(), "-10");
        assertEquals(sut.roll()[0].total, -10);
    }
    
    @Test
    public void testSingleDie() {
        // GIVEN
        DiceExpression sut = DiceExpressionParser.parse("1d4");
        
        // THEN
        assertEquals(sut.toString(), "1d4");
        this.assertResultsWithinRange(sut, 4, 1, 1, 4, 1);
    }
    
    @Test
    public void testDieWithModifier() {
        // GIVEN
        DiceExpression sut = DiceExpressionParser.parse("1d4-10");
        
        // THEN
        assertEquals(sut.toString(), "1d4-10");
        this.assertResultsWithinRange(sut, 4, 1, 1-10, 4-10, 1);
    }
    
    @Test
    public void testDieWithModifierSpaces() {
        // GIVEN
        DiceExpression sut = DiceExpressionParser.parse("1d4 -10");
        
        // THEN
        assertEquals(sut.toString(), "1d4-10");
        this.assertResultsWithinRange(sut, 4, 1, 1-10, 4-10, 1);
    }
    
    @Test
    public void testNegativeDie() {
        // GIVEN
        DiceExpression sut = DiceExpressionParser.parse("-1d8");
        
        // THEN
        assertEquals(sut.toString(), "-1d8");
        this.assertResultsWithinRange(sut, 8, 1, -8, -1, 1);
    }
    
    @Test
    public void testMultipleDiceWithModifier() {
        // GIVEN
        DiceExpression sut = DiceExpressionParser.parse("4d4+2");
        
        // THEN
        assertEquals(sut.toString(), "4d4+2");
        this.assertResultsWithinRange(sut, 4, 4, 6, (4*4)+2, 1);
    }
    
    @Test
    public void testMultipleDiceTypes() {
        // GIVEN
        DiceExpression sut = DiceExpressionParser.parse("2d4+1d100");
        
        // THEN
        assertEquals(sut.toString(), "2d4+1d100");
        this.assertResultsWithinRange(sut, 100, 3, 3, (4*2)+100, 1);
    }
    
    @Test
    public void testMultipleDiceTypesWithModifier() {
        // GIVEN
        DiceExpression sut = DiceExpressionParser.parse("2d6+2d4+1d100+10");
        
        // THEN
        assertEquals(sut.toString(), "2d6+2d4+1d100+10");
        this.assertResultsWithinRange(sut, 100, 5, 15, (6*2)+(4*2)+100+10, 1);
    }
    
    @Test
    public void testWithAdvantage() {
        // GIVEN
        DiceExpression sut = DiceExpressionParser.parse("ADV 1d20+4");
        
        // THEN
        assertEquals(sut.toString(), "ADV 1d20+4");
        this.assertResultsWithinRange(sut, 20, 2, 5, 25, 1);
    }
    
    @Test
    public void testWithDisadvantage() {
        // GIVEN
        DiceExpression sut = DiceExpressionParser.parse("DIS 1d20");
        
        // THEN
        assertEquals(sut.toString(), "DIS 1d20");
        this.assertResultsWithinRange(sut, 20, 2, 1, 20, 1);
    }
    
    @Test
    public void testSingleDieRepeated() {
        // GIVEN
        DiceExpression sut = DiceExpressionParser.parse("2x 1d4+1");
        
        // THEN
        assertEquals(sut.toString(), "2x 1d4+1");
        this.assertResultsWithinRange(sut, 4, 1, 2, 5, 2);
    }
    
    private void assertResultsWithinRange(DiceExpression expression, 
            int maxDiceRoll,
            int expectedNumberOfRolls,
            int minTotal, 
            int maxTotal,
            int repetitions) {
        Set<Integer> foundResults = new HashSet();
        for (int i = 0; i < 500; ++i) {
            RollResult[] results = expression.roll();
            assertEquals(repetitions, results.length);
            for(RollResult roll: results) {
                assertEquals(roll.individualRolls.size(), expectedNumberOfRolls);
                for (int r : roll.individualRolls) {
                    assetGreaterEqualThan(r, 1);
                    assetLesserEqualThan(r, maxDiceRoll);
                }
                assetGreaterEqualThan(roll.total, minTotal);
                assetLesserEqualThan(roll.total, maxTotal);
                foundResults.add(roll.total);
            }
        }
        assertTrue(foundResults.size() > 1);
    }
    
    private static void assetLesserEqualThan(int x, int y) {
        assertTrue(String.format("%d is not <= %d", x, y), x <= y);
    }
    
    private static void assetGreaterEqualThan(int x, int y) {
                assertTrue(String.format("%d is not >= %d", x, y), x >= y);
    }
}
