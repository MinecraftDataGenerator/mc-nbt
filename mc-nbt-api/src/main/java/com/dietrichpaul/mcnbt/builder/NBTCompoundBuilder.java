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

import com.dietrichpaul.mcnbt.*;
import com.dietrichpaul.mcnbt.primitive.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A fluent builder class for constructing an {@link NBTCompound} structure.
 * Provides a clean and readable way to define NBT data before insertion.
 * <p>
 * The Builder uses a temporary {@code LinkedHashMap} to maintain insertion order
 * while collecting tags, ensuring the final {@code NBTCompound} respects this order.
 * </p>
 */
public class NBTCompoundBuilder {
    // Use LinkedHashMap to preserve insertion order, matching NBTCompound's behavior
    private final Map<String, NBTTag<?>> tags = new LinkedHashMap<>();

    /**
     * Creates a new builder instance.
     */
    public NBTCompoundBuilder() {
    }

    /**
     * Puts a TAG_Byte into the compound.
     *
     * @param key   The name of the tag.
     * @param value The byte value.
     * @return This Builder, for chaining.
     */
    public NBTCompoundBuilder putByte(String key, byte value) {
        tags.put(key, NBTByte.of(value));
        return this;
    }

    /**
     * Puts a TAG_Short into the compound.
     *
     * @param key   The name of the tag.
     * @param value The short value.
     * @return This Builder, for chaining.
     */
    public NBTCompoundBuilder putShort(String key, short value) {
        tags.put(key, NBTShort.of(value));
        return this;
    }

    /**
     * Puts a TAG_Int into the compound.
     *
     * @param key   The name of the tag.
     * @param value The int value.
     * @return This Builder, for chaining.
     */
    public NBTCompoundBuilder putInt(String key, int value) {
        tags.put(key, NBTInt.of(value));
        return this;
    }

    /**
     * Puts a TAG_Long into the compound.
     *
     * @param key   The name of the tag.
     * @param value The long value.
     * @return This Builder, for chaining.
     */
    public NBTCompoundBuilder putLong(String key, long value) {
        tags.put(key, NBTLong.of(value));
        return this;
    }

    /**
     * Puts a TAG_Float into the compound.
     *
     * @param key   The name of the tag.
     * @param value The float value.
     * @return This Builder, for chaining.
     */
    public NBTCompoundBuilder putFloat(String key, float value) {
        tags.put(key, NBTFloat.of(value));
        return this;
    }

    /**
     * Puts a TAG_Double into the compound.
     *
     * @param key   The name of the tag.
     * @param value The double value.
     * @return This Builder, for chaining.
     */
    public NBTCompoundBuilder putDouble(String key, double value) {
        tags.put(key, NBTDouble.of(value));
        return this;
    }

    /**
     * Puts a TAG_String into the compound.
     *
     * @param key   The name of the tag.
     * @param value The string value.
     * @return This Builder, for chaining.
     */
    public NBTCompoundBuilder putString(String key, String value) {
        tags.put(key, NBTString.of(value));
        return this;
    }

    /**
     * Puts an existing {@link NBTTag} into the compound.
     *
     * @param key The name of the tag.
     * @param tag The NBT tag object.
     * @return This Builder, for chaining.
     */
    public NBTCompoundBuilder put(String key, NBTTag<?> tag) {
        tags.put(key, tag);
        return this;
    }

    /**
     * Starts a sub-building process for a nested {@link NBTCompound} (TAG_Compound).
     * The built sub-compound is put into this compound under the given key.
     *
     * @param key                The name of the nested compound tag.
     * @param subCompoundBuilder A consumer that accepts a new {@link NBTCompoundBuilder}
     *                           to define the contents of the nested compound.
     * @return This Builder, for chaining.
     */
    public NBTCompoundBuilder compound(String key, Consumer<NBTCompoundBuilder> subCompoundBuilder) {
        NBTCompoundBuilder subNBTCompoundBuilder = new NBTCompoundBuilder();
        subCompoundBuilder.accept(subNBTCompoundBuilder);
        this.tags.put(key, subNBTCompoundBuilder.build());
        return this;
    }

    /**
     * Starts a sub-building process for a nested {@link NBTList} (TAG_List).
     * The built list is put into this compound under the given key.
     *
     * @param key            The name of the nested list tag.
     * @param subListBuilder A consumer that accepts a new {@link NBTListBuilder}
     *                       to define the contents of the nested list.
     * @return This Builder, for chaining.
     */
    public NBTCompoundBuilder list(String key, Consumer<NBTListBuilder<?>> subListBuilder) {
        NBTListBuilder<?> builder = new NBTListBuilder<>();
        subListBuilder.accept(builder);
        this.tags.put(key, builder.build());
        return this;
    }

    /**
     * Starts a sub-building process for a nested {@link NBTByteArray} (TAG_Byte_Array).
     * The built byte array is put into this compound under the given key.
     *
     * @param key             The name of the nested byte array tag.
     * @param subArrayBuilder A consumer that accepts a new {@link NBTByteArrayBuilder}
     *                        to define the contents of the nested array.
     * @return This Builder, for chaining.
     */
    public NBTCompoundBuilder byteArray(String key, Consumer<NBTByteArrayBuilder> subArrayBuilder) {
        NBTByteArrayBuilder builder = new NBTByteArrayBuilder();
        subArrayBuilder.accept(builder);
        this.tags.put(key, builder.build());
        return this;
    }

    /**
     * Starts a sub-building process for a nested {@link NBTIntArray} (TAG_Int_Array).
     * The built int array is put into this compound under the given key.
     *
     * @param key             The name of the nested int array tag.
     * @param subArrayBuilder A consumer that accepts a new {@link NBTIntArrayBuilder}
     *                        to define the contents of the nested array.
     * @return This Builder, for chaining.
     */
    public NBTCompoundBuilder intArray(String key, Consumer<NBTIntArrayBuilder> subArrayBuilder) {
        NBTIntArrayBuilder builder = new NBTIntArrayBuilder();
        subArrayBuilder.accept(builder);
        this.tags.put(key, builder.build());
        return this;
    }

    /**
     * Starts a sub-building process for a nested {@link NBTLongArray} (TAG_Long_Array).
     * The built long array is put into this compound under the given key.
     *
     * @param key             The name of the nested long array tag.
     * @param subArrayBuilder A consumer that accepts a new {@link NBTLongArrayBuilder}
     *                        to define the contents of the nested array.
     * @return This Builder, for chaining.
     */
    public NBTCompoundBuilder longArray(String key, Consumer<NBTLongArrayBuilder> subArrayBuilder) {
        NBTLongArrayBuilder builder = new NBTLongArrayBuilder();
        subArrayBuilder.accept(builder);
        this.tags.put(key, builder.build());
        return this;
    }

    /**
     * Finalizes the building process and constructs the immutable {@link NBTCompound}
     * containing all added tags.
     *
     * @return The fully constructed {@link NBTCompound}.
     */
    public NBTCompound build() {
        NBTCompound compound = new NBTCompound();
        for (Map.Entry<String, NBTTag<?>> entry : tags.entrySet()) {
            compound.put(new NBTTagIdentifiable<>(entry.getKey(), entry.getValue()));
        }
        return compound;
    }
}