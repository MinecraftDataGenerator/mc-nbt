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

import com.dietrichpaul.mcnbt.NBTByteArray;
import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;

public class NBTByteArrayBuilder {
    private final TByteList content;

    public NBTByteArrayBuilder() {
        this.content = new TByteArrayList();
    }

    public NBTByteArrayBuilder(int initialCapacity) {
        this.content = new TByteArrayList(initialCapacity);
    }

    public NBTByteArrayBuilder add(byte value) {
        content.add(value);
        return this;
    }

    public NBTByteArrayBuilder add(byte[] values) {
        content.add(values);
        return this;
    }

    public NBTByteArray build() {
        return new NBTByteArray(content);
    }
}