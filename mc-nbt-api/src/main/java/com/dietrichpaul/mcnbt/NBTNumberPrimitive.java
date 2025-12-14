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
 * An {@link NBTPrimitive} whose underlying value is a {@link Number}.
 * <p>
 * This interface provides default implementations for all primitive and string
 * conversions based on {@link Number}'s widening/narrowing conversion methods.
 * Implementations only need to supply the boxed numeric value via
 * {@link #getPrimitiveType()} and the appropriate {@link NBTTagType}.
 * <ul>
 *     <li>{@link #asString()} delegates to {@link Number#toString()}.</li>
 *     <li>{@link #asBoolean()} returns {@code true} if {@code intValue() != 0}.</li>
 *     <li>{@link #asByte()} delegates to {@link Number#byteValue()}.</li>
 *     <li>{@link #asShort()} delegates to {@link Number#shortValue()}.</li>
 *     <li>{@link #asInt()} delegates to {@link Number#intValue()}.</li>
 *     <li>{@link #asLong()} delegates to {@link Number#longValue()}.</li>
 *     <li>{@link #asFloat()} delegates to {@link Number#floatValue()}.</li>
 *     <li>{@link #asDouble()} delegates to {@link Number#doubleValue()}.</li>
 * </ul>
 *
 * @param <T> the boxed numeric type stored by this tag
 */
public interface NBTNumberPrimitive<T extends Number> extends NBTPrimitive<T> {

    /**
     * {@inheritDoc}
     * <p>
     * Default implementation returns {@code getPrimitiveType().doubleValue()}.
     */
    @Override
    default double asDouble() {
        return getPrimitiveType().doubleValue();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Default implementation returns {@code getPrimitiveType().toString()}.
     */
    @Override
    default String asString() {
        return getPrimitiveType().toString();
    }

    /**
     * {@inheritDoc}
     * <p>
     * For numeric primitives, this is {@code getPrimitiveType().intValue() != 0}.
     */
    @Override
    default boolean asBoolean() {
        return getPrimitiveType().intValue() != 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Default implementation returns {@code getPrimitiveType().byteValue()}.
     */
    @Override
    default byte asByte() {
        return getPrimitiveType().byteValue();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Default implementation returns {@code getPrimitiveType().shortValue()}.
     */
    @Override
    default short asShort() {
        return getPrimitiveType().shortValue();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Default implementation returns {@code getPrimitiveType().intValue()}.
     */
    @Override
    default int asInt() {
        return getPrimitiveType().intValue();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Default implementation returns {@code getPrimitiveType().longValue()}.
     */
    @Override
    default long asLong() {
        return getPrimitiveType().longValue();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Default implementation returns {@code getPrimitiveType().floatValue()}.
     */
    @Override
    default float asFloat() {
        return getPrimitiveType().floatValue();
    }
}
