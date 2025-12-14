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

package com.dietrichpaul.mcnbt.builder;

import com.dietrichpaul.mcnbt.NBTList;
import com.dietrichpaul.mcnbt.NBTTag;
import com.dietrichpaul.mcnbt.primitive.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A fluent builder for constructing {@link com.dietrichpaul.mcnbt.NBTList} instances.
 * <p>
 * Elements are collected in a temporary {@link java.util.List} and transformed
 * into an {@link NBTList} when {@link #build()} is called.
 * </p>
 *
 * @param <T> the element type contained in the resulting list
 */
public class NBTListBuilder<T extends NBTTag<?>> {
    private final List<T> list = new ArrayList<>();

    /**
     * Creates a new list builder.
     */
    public NBTListBuilder() {
    }

    /**
     * Adds an arbitrary tag to the list.
     *
     * @param tag the tag to add
     * @return this builder for chaining
     */
    public NBTListBuilder<T> add(T tag) {
        list.add(tag);
        return this;
    }

    /**
     * Adds a {@code TAG_Byte} to the list.
     *
     * @param value the byte value
     * @return this builder for chaining
     */
    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> putByte(byte value) {
        list.add((T) NBTByte.of(value));
        return this;
    }

    /**
     * Adds a {@code TAG_Short} to the list.
     *
     * @param value the short value
     * @return this builder for chaining
     */
    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> putShort(short value) {
        list.add((T) NBTShort.of(value));
        return this;
    }

    /**
     * Adds a {@code TAG_Int} to the list.
     *
     * @param value the int value
     * @return this builder for chaining
     */
    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> putInt(int value) {
        list.add((T) NBTInt.of(value));
        return this;
    }

    /**
     * Adds a {@code TAG_Long} to the list.
     *
     * @param value the long value
     * @return this builder for chaining
     */
    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> putLong(long value) {
        list.add((T) NBTLong.of(value));
        return this;
    }

    /**
     * Adds a {@code TAG_Float} to the list.
     *
     * @param value the float value
     * @return this builder for chaining
     */
    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> putFloat(float value) {
        list.add((T) NBTFloat.of(value));
        return this;
    }

    /**
     * Adds a {@code TAG_Double} to the list.
     *
     * @param value the double value
     * @return this builder for chaining
     */
    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> putDouble(double value) {
        list.add((T) NBTDouble.of(value));
        return this;
    }

    /**
     * Adds a {@code TAG_String} to the list.
     *
     * @param value the string value
     * @return this builder for chaining
     */
    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> putString(String value) {
        list.add((T) NBTString.of(value));
        return this;
    }

    /**
     * Adds a nested {@code TAG_Compound} to the list using a sub-builder.
     *
     * @param subCompoundBuilder a consumer that configures the {@link NBTCompoundBuilder}
     * @return this builder for chaining
     */
    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> compound(Consumer<NBTCompoundBuilder> subCompoundBuilder) {
        NBTCompoundBuilder builder = new NBTCompoundBuilder();
        subCompoundBuilder.accept(builder);
        list.add((T) builder.build());
        return this;
    }

    /**
     * Adds a nested {@code TAG_List} to the list using a sub-builder.
     *
     * @param subListBuilder a consumer that configures the {@link NBTListBuilder}
     * @return this builder for chaining
     */
    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> list(Consumer<NBTListBuilder<?>> subListBuilder) {
        NBTListBuilder<?> builder = new NBTListBuilder<>();
        subListBuilder.accept(builder);
        list.add((T) builder.build());
        return this;
    }

    /**
     * Adds a {@code TAG_Byte_Array} to the list using a sub-builder.
     *
     * @param subArrayBuilder a consumer that configures the {@link NBTByteArrayBuilder}
     * @return this builder for chaining
     */
    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> byteArray(Consumer<NBTByteArrayBuilder> subArrayBuilder) {
        NBTByteArrayBuilder builder = new NBTByteArrayBuilder();
        subArrayBuilder.accept(builder);
        list.add((T) builder.build());
        return this;
    }

    /**
     * Adds a {@code TAG_Int_Array} to the list using a sub-builder.
     *
     * @param subArrayBuilder a consumer that configures the {@link NBTIntArrayBuilder}
     * @return this builder for chaining
     */
    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> intArray(Consumer<NBTIntArrayBuilder> subArrayBuilder) {
        NBTIntArrayBuilder builder = new NBTIntArrayBuilder();
        subArrayBuilder.accept(builder);
        list.add((T) builder.build());
        return this;
    }

    /**
     * Adds a {@code TAG_Long_Array} to the list using a sub-builder.
     *
     * @param subArrayBuilder a consumer that configures the {@link NBTLongArrayBuilder}
     * @return this builder for chaining
     */
    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> longArray(Consumer<NBTLongArrayBuilder> subArrayBuilder) {
        NBTLongArrayBuilder builder = new NBTLongArrayBuilder();
        subArrayBuilder.accept(builder);
        list.add((T) builder.build());
        return this;
    }

    /**
     * Builds the {@link NBTList} from the currently collected elements.
     *
     * @return a new {@link NBTList} containing the collected tags
     */
    public NBTList<T> build() {
        return NBTList.of(list);
    }
}