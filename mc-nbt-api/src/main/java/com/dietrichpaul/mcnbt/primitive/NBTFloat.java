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
 * Represents an NBT tag containing a 32-bit floating point number.
 */
public class NBTFloat implements NBTNumberPrimitive<Float> {

    private float value;

    private NBTFloat(float value) {
        this.value = value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public static NBTFloat of(float value) {
        return new NBTFloat(value);
    }

    @Override
    public Float getPrimitiveType() {
        return value;
    }


    @Override
    public float asFloat() {
        return value;
    }

    @Override
    public NBTTagType getTagType() {
        return NBTTagType.FLOAT;
    }
}