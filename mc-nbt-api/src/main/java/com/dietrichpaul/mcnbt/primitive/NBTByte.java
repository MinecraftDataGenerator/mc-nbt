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
 * Represents an NBT tag containing an 8-bit signed integer.
 */
public class NBTByte implements NBTNumberPrimitive<Byte> {

    private byte value;

    private NBTByte(byte value) {
        this.value = value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    public static NBTByte of(byte value) {
        return new NBTByte(value);
    }

    @Override
    public Byte getPrimitiveType() {
        return value;
    }

    @Override
    public byte asByte() {
        return value;
    }

    @Override
    public NBTTagType getTagType() {
        return NBTTagType.BYTE;
    }

}