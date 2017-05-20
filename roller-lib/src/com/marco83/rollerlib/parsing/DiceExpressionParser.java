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
package com.marco83.rollerlib.parsing;

import com.marco83.rollerlib.DiceExpression;
import com.marco83.rollerlib.Die;
import com.marco83.rollerlib.Format;
import com.marco83.rollerlib.RollType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author marcoconti
 */
public class DiceExpressionParser {
    
    private static class Token {
        public Integer multiplier;
        public String value;
        
        Token(Integer multiplier, String value) {
            this.multiplier = multiplier;
            this.value = value;
        }

        @Override
        public String toString() {
            return Format.asMultiplier(this.multiplier) + this.value;
        } 
        
        Boolean isNumeric() {
            return this.value.matches("^\\d+?"); 
        }
        
        Integer numericValue() {
            return this.multiplier * Integer.parseInt(this.value);
        }
            
        private static final Pattern DICE_PATTERN = Pattern.compile("(\\d+)d(\\d+)");

        Die dieValue() throws IllegalArgumentException {
            Matcher p = DICE_PATTERN.matcher(this.value);
            if (!p.matches()) {
                throw new IllegalArgumentException(String.format("Unable to parse token '%s'", this.value));
            }
            Integer rolls = Integer.parseInt(p.group(1));
            Integer faces = Integer.parseInt(p.group(2));

            return new Die(rolls, faces, this.multiplier);
        }
    }
    
    private static List<Token> tokenizeString(String text) throws IllegalArgumentException {
        
        List<Token> tokens = new ArrayList();
        while (!text.isEmpty()) {
            Integer minusPosition = text.lastIndexOf('-');
            Integer plusPosition = text.lastIndexOf('+');
            if (minusPosition < 0 && plusPosition < 0) { // done!
                tokens.add(0, new Token(1, text.trim()));
                break;
            }
            
            if (minusPosition == text.length()-1 || plusPosition == text.length()-1) {
                throw new IllegalArgumentException("String ends with a + or -");
            }
            
            Integer cutOffPosition;
            Integer multiplier;
            if (minusPosition >= 0 && (plusPosition < 0 || plusPosition < minusPosition)) {
                // it's a minus
                cutOffPosition = minusPosition;
                multiplier = -1;
            }
            else { // it's a plus
                cutOffPosition = plusPosition;
                multiplier = +1;
            }
            
            String token = text.substring(cutOffPosition+1).trim();
            String rest = text.substring(0, cutOffPosition).trim();
            text = rest;
            tokens.add(0, new Token(multiplier, token));
        }
        
        return tokens;
    }
    
    private static DiceExpression sumOfTokens(List<Token> tokens, RollType type) throws IllegalArgumentException {
        Integer totalModifier = 0;
        List<Die> dice = new ArrayList();
        for(Token token : tokens) {
            if (token.isNumeric()) {
                totalModifier += token.numericValue();
            } else {
                Die die = token.dieValue();
                dice.add(die);
            }
        }
        
        // for ADV/DIS, first one should be d20
        if (type == RollType.advantage || type == RollType.disadvantage) {
            if (dice.isEmpty() || dice.get(0).faces != 20 || dice.get(0).rolls != 1) {
                throw new IllegalArgumentException("Can't user ADV/DIS on non-d20 rolls");
            }
        }
        return new DiceExpression(dice, totalModifier, type);
    }
    
    public static DiceExpression parse(String text) throws IllegalArgumentException {
        String cleanText = text.toLowerCase().trim();
        
        RollType type;
        if (cleanText.startsWith("adv")) {
            type = RollType.advantage;
            cleanText = cleanText.substring(3);
        } else if (cleanText.startsWith("dis")) {
            type = RollType.disadvantage;
            cleanText = cleanText.substring(3);
        } else {
            type = RollType.regular;
        }
        
        List<Token> tokens = tokenizeString(cleanText);
        return sumOfTokens(tokens, type);
    }
    
}
