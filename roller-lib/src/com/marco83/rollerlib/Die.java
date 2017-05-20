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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author marcoconti
 */
public class Die {
    
    private static final Random random = new Random();
    
    public final Integer faces;
    public final Integer rolls;
    public final Integer multiplier;

    public Die(int rolls, int faces, int multiplier) {
        this.faces = faces;
        this.multiplier = multiplier;
        this.rolls = rolls;
    }
    
    /**
     * Rolls all dice
     * @return the individual roll results
     */
    public RollResult roll() {
        return this.roll(RollType.regular);
    }
    /**
     * Rolls all dice
     * @param type roll type
     * @return the individual roll results
     */
    public RollResult roll(RollType type) {
        List<Integer> results = new ArrayList();
        int total = 0;
        for (int i = 0; i < this.rolls; i++) {
            int result;
            if ((type == RollType.advantage || type == RollType.disadvantage) && this.faces == 20) {
                int result1 = 1+random.nextInt(this.faces);
                int result2 = 1+random.nextInt(this.faces);
                results.add(result1);
                results.add(result2);
                if (type == RollType.advantage) {
                    result = Math.max(result1, result2);
                } else {
                    result = Math.min(result1, result2);
                }
            } else {
                result = 1+random.nextInt(this.faces);
                results.add(result);
            }
            total += result;
        }
        return new RollResult(results, multiplier * total);
    }

    @Override
    public String toString() {
        return String.format("%s%dd%d", Format.asMultiplier(this.multiplier), this.rolls, this.faces);
    }
}