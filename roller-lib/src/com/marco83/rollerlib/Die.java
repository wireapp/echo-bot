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
        return this.roll(false);
    }
    /**
     * Rolls all dice
     * @param maximiseRoll if true, rolls will always return the maximum face value
     * @return the individual roll results
     */
    public RollResult roll(boolean maximiseRoll) {
        List<Integer> results = new ArrayList();
        for (int i = 0; i < this.rolls; i++) {
            int result = maximiseRoll ? this.faces : 1 + random.nextInt(this.faces);
            results.add(result);
        }
        Integer total = this.multiplier * results.stream().reduce(0, Integer::sum);
        return new RollResult(results, total);
    }

    @Override
    public String toString() {
        return String.format("%s%dd%d", Format.asMultiplier(this.multiplier), this.rolls, this.faces);
    }
}