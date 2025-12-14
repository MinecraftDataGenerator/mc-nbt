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

import com.dietrichpaul.mcnbt.NBTIntArray;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

public class NBTIntArrayBuilder {
    private final TIntList content;

    public NBTIntArrayBuilder() {
        this.content = new TIntArrayList();
    }

    public NBTIntArrayBuilder(int initialCapacity) {
        this.content = new TIntArrayList(initialCapacity);
    }

    public NBTIntArrayBuilder add(int value) {
        content.add(value);
        return this;
    }

    public NBTIntArrayBuilder add(int[] values) {
        content.add(values);
        return this;
    }

    public NBTIntArray build() {
        return new NBTIntArray(content);
    }
}