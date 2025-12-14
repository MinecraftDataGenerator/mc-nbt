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

/**
 * Represents an NBT tag with a primitive (non-collection) value.
 * <p>
 * Each concrete primitive tag must implement conversions into all standard
 * numeric types explicitly. The asX() methods are abstract to force
 * type-specific behavior.
 *
 * @param <T> the underlying primitive value type
 */
public interface NBTPrimitive<T> extends NBTTag<T> {

    /**
     * Retrieves the primitive value of this tag.
     *
     * @return the primitive value
     */
    T getPrimitiveType();

    /**
     * Retrieves the value of this tag as a {@link String}.
     *
     * @return the value as a {@link String}
     */
    String asString();

    /**
     * Retrieves the value of this tag as a {@code boolean}.
     *
     * @return the value as a {@code boolean}
     */
    boolean asBoolean();

    /**
     * Retrieves the value of this tag as a {@code byte}.
     *
     * @return the value as a {@code byte}
     */
    byte asByte();

    /**
     * Retrieves the value of this tag as a {@code short}.
     *
     * @return the value as a {@code short}
     */
    short asShort();

    /**
     * Retrieves the value of this tag as a {@code int}.
     *
     * @return the value as an {@code int}
     */
    int asInt();

    /**
     * Retrieves the value of this tag as a {@code long}.
     *
     * @return the value as a {@code long}
     */
    long asLong();

    /**
     * Retrieves the value of this tag as a {@code float}.
     *
     * @return the value as a {@code float}
     */
    float asFloat();

    /**
     * Retrieves the value of this tag as a {@code double}.
     *
     * @return the value as a {@code double}
     */
    double asDouble();
}
