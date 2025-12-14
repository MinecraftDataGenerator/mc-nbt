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