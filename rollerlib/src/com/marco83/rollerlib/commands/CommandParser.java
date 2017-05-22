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

import com.marco83.rollerlib.dice.DiceExpression;
import com.marco83.rollerlib.dice.RollResult;
import com.marco83.rollerlib.dice.parsing.DiceExpressionParser;

/**
 *
 * @author marcoconti
 */

public class CommandParser {
    
    public final String prefix;

    public CommandParser() {
        this.prefix = "/roll";
    }

    public CommandParser(String prefix) {
        this.prefix = prefix;
    }
    
    public String parseText(String text) {
        if(text.trim().equals("/help")) {
            return "**Dice Roller Bot**\n" +
                    "Type `/roll 3d6+6` to parse and roll\n" +
                    "also supports:\n" +
                    "- advantage/disadvantage (`ADV 1d20+10`) or (`DIS 1d20+10`)\n" +
                    "- repetitions of entire expression (`4x 1d20+10`)\n" +
                    "- combination of dice (`1d20+10-3d6-5+1d4`)\n" +
                    "- omit `/roll` for quick input, but will not print errors in case of errors\n";
        }
        
        if (text.startsWith(this.prefix)) { // is this defintely a roll?
            return this.executeCommand(text.substring(this.prefix.length()).trim());
        }
        
        // try to parse it directly as a roll, if it fails, don't warn
        try {
            DiceExpression expression = DiceExpressionParser.parse(text);
            return this.describeResults(expression);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
    
    private String executeCommand(String command) {
        DiceExpression expression;
        try {
            expression = DiceExpressionParser.parse(command);
        } catch (IllegalArgumentException ex) {
            return "ERROR: "+ex.getMessage();
        }
        return this.describeResults(expression);
    }
    
    private String describeResults(DiceExpression expression) {
        RollResult results[] = expression.roll();
        String output = "";
        for(RollResult r: results) {
            output += r.toString() + "\n";
        }
        return output.trim();
    }
}
