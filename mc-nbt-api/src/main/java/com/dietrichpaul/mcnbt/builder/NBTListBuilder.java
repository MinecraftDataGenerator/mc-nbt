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

public class NBTListBuilder<T extends NBTTag<?>> {
    private final List<T> list = new ArrayList<>();

    public NBTListBuilder() {}

    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> add(T tag) {
        list.add(tag);
        return this;
    }

    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> putByte(byte value) {
        list.add((T) NBTByte.of(value));
        return this;
    }

    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> putShort(short value) {
        list.add((T) NBTShort.of(value));
        return this;
    }

    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> putInt(int value) {
        list.add((T) NBTInt.of(value));
        return this;
    }

    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> putLong(long value) {
        list.add((T) NBTLong.of(value));
        return this;
    }

    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> putFloat(float value) {
        list.add((T) NBTFloat.of(value));
        return this;
    }

    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> putDouble(double value) {
        list.add((T) NBTDouble.of(value));
        return this;
    }

    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> putString(String value) {
        list.add((T) NBTString.of(value));
        return this;
    }

    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> compound(Consumer<NBTCompoundBuilder> subCompoundBuilder) {
        NBTCompoundBuilder builder = new NBTCompoundBuilder();
        subCompoundBuilder.accept(builder);
        list.add((T) builder.build());
        return this;
    }

    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> list(Consumer<NBTListBuilder<?>> subListBuilder) {
        NBTListBuilder<?> builder = new NBTListBuilder<>();
        subListBuilder.accept(builder);
        list.add((T) builder.build());
        return this;
    }

    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> byteArray(Consumer<NBTByteArrayBuilder> subArrayBuilder) {
        NBTByteArrayBuilder builder = new NBTByteArrayBuilder();
        subArrayBuilder.accept(builder);
        list.add((T) builder.build());
        return this;
    }

    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> intArray(Consumer<NBTIntArrayBuilder> subArrayBuilder) {
        NBTIntArrayBuilder builder = new NBTIntArrayBuilder();
        subArrayBuilder.accept(builder);
        list.add((T) builder.build());
        return this;
    }

    @SuppressWarnings("unchecked")
    public NBTListBuilder<T> longArray(Consumer<NBTLongArrayBuilder> subArrayBuilder) {
        NBTLongArrayBuilder builder = new NBTLongArrayBuilder();
        subArrayBuilder.accept(builder);
        list.add((T) builder.build());
        return this;
    }

    public NBTList<T> build() {
        return NBTList.of(list);
    }
}