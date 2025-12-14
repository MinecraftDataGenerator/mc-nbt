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
 * Strings are considered primitive in NBT. Provides conversion to string
 * and boolean (non-empty check).
 */
public class NBTString implements NBTPrimitive<String> {

    private String value;

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

    @Override
    public String getPrimitiveType() {
        return value;
    }

    @Override
    public boolean asBoolean() {
        return value != null && !value.isEmpty();
    }

    @Override
    public byte asByte() {
        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public short asShort() {
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public int asInt() {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public long asLong() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    @Override
    public float asFloat() {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    @Override
    public double asDouble() {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public NBTTagType getTagType() {
        return NBTTagType.STRING;
    }
}
