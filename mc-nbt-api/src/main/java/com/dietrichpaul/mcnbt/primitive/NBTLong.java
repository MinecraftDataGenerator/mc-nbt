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
 * Represents an NBT tag containing a 64-bit signed integer ("long").
 * <p>
 * This is the NBT type corresponding to {@link com.dietrichpaul.mcnbt.NBTTagType#LONG}.
 */
public class NBTLong implements NBTNumberPrimitive<Long> {

    private long value;

    /**
     * Creates a new long tag with the given value.
     *
     * @param value the long value to store
     */
    private NBTLong(long value) {
        this.value = value;
    }

    /**
     * Factory method for creating a new {@code NBTLong}.
     *
     * @param value the long value to store
     * @return a new {@code NBTLong} containing {@code value}
     */
    public static NBTLong of(long value) {
        return new NBTLong(value);
    }

    /**
     * Updates the stored long value.
     *
     * @param value the new long value
     */
    public void setValue(long value) {
        this.value = value;
    }

    /**
     * Returns the boxed primitive value.
     *
     * @return the stored {@link Long}
     */
    @Override
    public Long getPrimitiveType() {
        return value;
    }

    /**
     * Returns the value as a Java {@code long}.
     *
     * @return the stored long value
     */
    @Override
    public long asLong() {
        return value;
    }

    /**
     * Returns the NBT tag type identifier for this tag.
     *
     * @return {@link com.dietrichpaul.mcnbt.NBTTagType#LONG}
     */
    @Override
    public NBTTagType getTagType() {
        return NBTTagType.LONG;
    }
}
