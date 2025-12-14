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
 * Represents an NBT tag containing a 32-bit floating point number ("float").
 * <p>
 * This is the NBT type corresponding to {@link com.dietrichpaul.mcnbt.NBTTagType#FLOAT}.
 */
public class NBTFloat implements NBTNumberPrimitive<Float> {

    private float value;

    /**
     * Creates a new float tag with the given value.
     *
     * @param value the float value to store
     */
    private NBTFloat(float value) {
        this.value = value;
    }

    /**
     * Factory method for creating a new {@code NBTFloat}.
     *
     * @param value the float value to store
     * @return a new {@code NBTFloat} containing {@code value}
     */
    public static NBTFloat of(float value) {
        return new NBTFloat(value);
    }

    /**
     * Updates the stored float value.
     *
     * @param value the new float value
     */
    public void setValue(float value) {
        this.value = value;
    }

    /**
     * Returns the boxed primitive value.
     *
     * @return the stored {@link Float}
     */
    @Override
    public Float getPrimitiveType() {
        return value;
    }


    /**
     * Returns the value as a Java {@code float}.
     *
     * @return the stored float value
     */
    @Override
    public float asFloat() {
        return value;
    }

    /**
     * Returns the NBT tag type identifier for this tag.
     *
     * @return {@link com.dietrichpaul.mcnbt.NBTTagType#FLOAT}
     */
    @Override
    public NBTTagType getTagType() {
        return NBTTagType.FLOAT;
    }
}