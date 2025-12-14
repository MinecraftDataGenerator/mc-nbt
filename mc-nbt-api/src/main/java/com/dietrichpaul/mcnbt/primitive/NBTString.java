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

import com.dietrichpaul.mcnbt.NBTPrimitive;
import com.dietrichpaul.mcnbt.NBTTagType;

/**
 * Represents an NBT tag containing a UTF-8 string (TAG_String).
 * <p>
 * This is the NBT type corresponding to {@link com.dietrichpaul.mcnbt.NBTTagType#STRING}.
 * Strings are considered primitive in NBT. Provides conversion to a Java {@code String}
 * and to {@code boolean} using a non-empty check.
 */
public class NBTString implements NBTPrimitive<String> {

    private String value;

    /**
     * Creates a new string tag with the given value.
     *
     * @param value the string value to store
     */
    private NBTString(String value) {
        this.value = value;
    }

    /**
     * Creates a new {@link NBTString} with the given value.
     *
     * @param value the string value
     * @return a new {@link NBTString} instance
     */
    public static NBTString of(String value) {
        return new NBTString(value);
    }

    /**
     * Updates the value of this string tag.
     *
     * @param value the new string value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns the boxed primitive value.
     *
     * @return the stored {@link String}
     */
    @Override
    public String getPrimitiveType() {
        return value;
    }

    /**
     * Returns {@code true} if the string is non-null and non-empty.
     *
     * @return whether the stored string is non-empty
     */
    @Override
    public boolean asBoolean() {
        return value != null && !value.isEmpty();
    }

    /**
     * Parses the value as a Java {@code byte}. Returns {@code 0} if parsing fails.
     *
     * @return the parsed byte value or {@code 0} on failure
     */
    @Override
    public byte asByte() {
        try {
            return Byte.parseByte(value);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Parses the value as a Java {@code short}. Returns {@code 0} if parsing fails.
     *
     * @return the parsed short value or {@code 0} on failure
     */
    @Override
    public short asShort() {
        try {
            return Short.parseShort(value);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Parses the value as a Java {@code int}. Returns {@code 0} if parsing fails.
     *
     * @return the parsed int value or {@code 0} on failure
     */
    @Override
    public int asInt() {
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Parses the value as a Java {@code long}. Returns {@code 0L} if parsing fails.
     *
     * @return the parsed long value or {@code 0L} on failure
     */
    @Override
    public long asLong() {
        try {
            return Long.parseLong(value);
        }
        catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * Parses the value as a Java {@code float}. Returns {@code 0f} if parsing fails.
     *
     * @return the parsed float value or {@code 0f} on failure
     */
    @Override
    public float asFloat() {
        try {
            return Float.parseFloat(value);
        }
        catch (NumberFormatException e) {
            return 0f;
        }
    }

    /**
     * Parses the value as a Java {@code double}. Returns {@code 0.0} if parsing fails.
     *
     * @return the parsed double value or {@code 0.0} on failure
     */
    @Override
    public double asDouble() {
        try {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Returns the value as a Java {@code String}.
     *
     * @return the stored string value
     */
    @Override
    public String asString() {
        return value;
    }

    /**
     * Returns the NBT tag type identifier for this tag.
     *
     * @return {@link com.dietrichpaul.mcnbt.NBTTagType#STRING}
     */
    @Override
    public NBTTagType getTagType() {
        return NBTTagType.STRING;
    }
}
