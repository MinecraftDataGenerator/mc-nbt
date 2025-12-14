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

import com.dietrichpaul.mcnbt.NBTLongArray;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

public class NBTLongArrayBuilder {
    private final TLongList content;

    public NBTLongArrayBuilder() {
        this.content = new TLongArrayList();
    }

    public NBTLongArrayBuilder(int initialCapacity) {
        this.content = new TLongArrayList(initialCapacity);
    }

    public NBTLongArrayBuilder add(long value) {
        content.add(value);
        return this;
    }

    public NBTLongArrayBuilder add(long[] values) {
        content.add(values);
        return this;
    }

    public NBTLongArray build() {
        return new NBTLongArray(content);
    }
}