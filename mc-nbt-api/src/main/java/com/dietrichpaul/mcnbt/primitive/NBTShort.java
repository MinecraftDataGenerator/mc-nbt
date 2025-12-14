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
 * Represents an NBT tag containing a 16-bit signed integer ("short").
 * <p>
 * This is the NBT type corresponding to {@link com.dietrichpaul.mcnbt.NBTTagType#SHORT}.
 */
public class NBTShort implements NBTNumberPrimitive<Short> {

    private short value;

    /**
     * Creates a new short tag with the given value.
     *
     * @param value the short value to store
     */
    private NBTShort(short value) {
        this.value = value;
    }

    /**
     * Factory method for creating a new {@code NBTShort}.
     *
     * @param value the short value to store
     * @return a new {@code NBTShort} containing {@code value}
     */
    public static NBTShort of(short value) {
        return new NBTShort(value);
    }

    /**
     * Updates the stored short value.
     *
     * @param value the new short value
     */
    public void setValue(short value) {
        this.value = value;
    }

    /**
     * Returns the boxed primitive value.
     *
     * @return the stored {@link Short}
     */
    @Override
    public Short getPrimitiveType() {
        return value;
    }

    /**
     * Returns the value as a Java {@code short}.
     *
     * @return the stored short value
     */
    @Override
    public short asShort() {
        return value;
    }

    /**
     * Returns the NBT tag type identifier for this tag.
     *
     * @return {@link com.dietrichpaul.mcnbt.NBTTagType#SHORT}
     */
    @Override
    public NBTTagType getTagType() {
        return NBTTagType.SHORT;
    }
}