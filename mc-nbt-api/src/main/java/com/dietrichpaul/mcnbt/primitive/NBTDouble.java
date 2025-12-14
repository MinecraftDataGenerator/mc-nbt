/*
 * Copyright (C) 2025 Paul Dietrich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dietrichpaul.mcnbt.primitive;

import com.dietrichpaul.mcnbt.NBTNumberPrimitive;
import com.dietrichpaul.mcnbt.NBTTagType;

/**
 * Represents an NBT tag containing a 64-bit floating point number ("double").
 * <p>
 * This is the NBT type corresponding to {@link com.dietrichpaul.mcnbt.NBTTagType#DOUBLE}.
 */
public class NBTDouble implements NBTNumberPrimitive<Double> {

    private double value;

    /**
     * Creates a new double tag with the given value.
     *
     * @param value the double value to store
     */
    private NBTDouble(double value) {
        this.value = value;
    }

    /**
     * Factory method for creating a new {@code NBTDouble}.
     *
     * @param value the double value to store
     * @return a new {@code NBTDouble} containing {@code value}
     */
    public static NBTDouble of(double value) {
        return new NBTDouble(value);
    }

    /**
     * Updates the stored double value.
     *
     * @param value the new double value
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Returns the boxed primitive value.
     *
     * @return the stored {@link Double}
     */
    @Override
    public Double getPrimitiveType() {
        return value;
    }

    /**
     * Returns the value as a Java {@code double}.
     *
     * @return the stored double value
     */
    @Override
    public double asDouble() {
        return value;
    }

    /**
     * Returns the NBT tag type identifier for this tag.
     *
     * @return {@link com.dietrichpaul.mcnbt.NBTTagType#DOUBLE}
     */
    @Override
    public NBTTagType getTagType() {
        return NBTTagType.DOUBLE;
    }
}