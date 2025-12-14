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
 */
public class NBTInt implements NBTNumberPrimitive<Integer> {

    private int value;

    private NBTInt(int value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static NBTInt of(int value) {
        return new NBTInt(value);
    }

    @Override
    public Integer getPrimitiveType() {
        return value;
    }

    @Override
    public int asInt() {
        return value;
    }

    @Override
    public NBTTagType getTagType() {
        return NBTTagType.INT;
    }
}