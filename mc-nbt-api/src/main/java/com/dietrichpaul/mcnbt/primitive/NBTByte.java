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
 * Represents an NBT tag containing an 8-bit signed integer ("byte").
 * <p>
 * This is the NBT type corresponding to {@link com.dietrichpaul.mcnbt.NBTTagType#BYTE}.
 */
public class NBTByte implements NBTNumberPrimitive<Byte> {

    private byte value;

    /**
     * Creates a new byte tag with the given value.
     *
     * @param value the byte value to store
     */
    private NBTByte(byte value) {
        this.value = value;
    }

    /**
     * Factory method for creating a new {@code NBTByte}.
     *
     * @param value the byte value to store
     * @return a new {@code NBTByte} containing {@code value}
     */
    public static NBTByte of(byte value) {
        return new NBTByte(value);
    }

    /**
     * Updates the stored byte value.
     *
     * @param value the new byte value
     */
    public void setValue(byte value) {
        this.value = value;
    }

    /**
     * Returns the boxed primitive value.
     *
     * @return the stored {@link Byte}
     */
    @Override
    public Byte getPrimitiveType() {
        return value;
    }

    /**
     * Returns the value as a Java {@code byte}.
     *
     * @return the stored byte value
     */
    @Override
    public byte asByte() {
        return value;
    }

    /**
     * Returns the NBT tag type identifier for this tag.
     *
     * @return {@link NBTTagType#BYTE}
     */
    @Override
    public NBTTagType getTagType() {
        return NBTTagType.BYTE;
    }

}