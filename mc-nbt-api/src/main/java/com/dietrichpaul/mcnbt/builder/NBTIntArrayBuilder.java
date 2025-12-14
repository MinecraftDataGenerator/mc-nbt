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