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
 * Represents an NBT tag containing a 32-bit signed integer.
 * <p>
 * This is the NBT type corresponding to {@link com.dietrichpaul.mcnbt.NBTTagType#INT}.
 */
public class NBTInt implements NBTNumberPrimitive<Integer> {

    private int value;

    /**
     * Creates a new int tag with the given value.
     *
     * @param value the int value to store
     */
    private NBTInt(int value) {
        this.value = value;
    }

    /**
     * Factory method for creating a new {@code NBTInt}.
     *
     * @param value the int value to store
     * @return a new {@code NBTInt} containing {@code value}
     */
    public static NBTInt of(int value) {
        return new NBTInt(value);
    }

    /**
     * Updates the stored int value.
     *
     * @param value the new int value
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Returns the boxed primitive value.
     *
     * @return the stored {@link Integer}
     */
    @Override
    public Integer getPrimitiveType() {
        return value;
    }

    /**
     * Returns the value as a Java {@code int}.
     *
     * @return the stored int value
     */
    @Override
    public int asInt() {
        return value;
    }

    /**
     * Returns the NBT tag type identifier for this tag.
     *
     * @return {@link com.dietrichpaul.mcnbt.NBTTagType#INT}
     */
    @Override
    public NBTTagType getTagType() {
        return NBTTagType.INT;
    }
}