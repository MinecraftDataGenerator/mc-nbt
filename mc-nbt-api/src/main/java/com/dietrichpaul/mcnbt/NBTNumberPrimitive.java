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

package com.dietrichpaul.mcnbt;

public interface NBTNumberPrimitive<T extends Number> extends NBTPrimitive<T> {

    @Override
    default double asDouble() {
        return getPrimitiveType().doubleValue();
    }

    @Override
    default String asString() {
        return getPrimitiveType().toString();
    }

    @Override
    default boolean asBoolean() {
        return getPrimitiveType().intValue() != 0;
    }

    @Override
    default byte asByte() {
        return getPrimitiveType().byteValue();
    }

    @Override
    default short asShort() {
        return getPrimitiveType().shortValue();
    }

    @Override
    default int asInt() {
        return getPrimitiveType().intValue();
    }

    @Override
    default long asLong() {
        return getPrimitiveType().longValue();
    }

    @Override
    default float asFloat() {
        return getPrimitiveType().floatValue();
    }
}
