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