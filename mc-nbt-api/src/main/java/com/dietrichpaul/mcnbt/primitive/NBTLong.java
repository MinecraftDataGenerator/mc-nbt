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
 * Represents an NBT tag containing a 64-bit signed integer.
 */
public class NBTLong implements NBTNumberPrimitive<Long> {

    private long value;

    private NBTLong(long value) {
        this.value = value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public static NBTLong of(long value) {
        return new NBTLong(value);
    }

    @Override
    public Long getPrimitiveType() {
        return value;
    }

    @Override
    public long asLong() {
        return value;
    }

    @Override
    public NBTTagType getTagType() {
        return NBTTagType.LONG;
    }
}
