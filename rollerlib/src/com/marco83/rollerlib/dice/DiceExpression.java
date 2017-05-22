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
package com.marco83.rollerlib.dice;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marcoconti
 */
public class DiceExpression {

    private final RollType type;
    private final int totalModifier;
    private final List<Die> dice;
    private final int repeat;
    
    public DiceExpression(int repeat, List<Die> dice, int totalModifier, RollType type) {
        this.dice = dice;
        this.totalModifier = totalModifier;
        this.type = type;
        this.repeat = repeat;
    }
    
    @Override
    public String toString() {
        String diceString = "";
        String repeatString = this.repeat != 1 
                ? String.format("%dx ", this.repeat) 
                : "";
        if (!this.dice.isEmpty()) {
            diceString = this.dice.get(0).toString();
            List<Die> otherDice = this.dice.subList(1, this.dice.size());
            diceString = otherDice.stream()
                    .map((Die d) -> {
                        if (d.multiplier == 1) {
                            return "+"+d.toString();
                        }
                        return d.toString();
                    })
                    .reduce(diceString, String::concat);
        }
        return 
            repeatString +
            (this.type != RollType.regular ? (this.type.prefix() + " ") : "") +    
            diceString +
            (this.totalModifier != 0 ? Format.withSign(this.totalModifier) : "");    
    }
    
    public RollResult[] roll() {
        List<RollResult> results = new ArrayList();
        for (int i = 0; i < this.repeat; i++) {
            List<Integer> rolls = new ArrayList();
            Integer total = 0;
            boolean foundD20 = false;
            for(Die die : this.dice) {
                RollResult r;
                if (!foundD20 && die.faces == 20) {
                    foundD20 = true;
                    r = die.roll(this.type);
                } else {
                    r = die.roll(RollType.regular);
                }
                rolls.addAll(r.individualRolls);
                total += r.total;
            }
            total += this.totalModifier;
            results.add(new RollResult(rolls, total));
        }
        RollResult[] output = new RollResult[this.repeat];
        return results.toArray(output);
    }
}

