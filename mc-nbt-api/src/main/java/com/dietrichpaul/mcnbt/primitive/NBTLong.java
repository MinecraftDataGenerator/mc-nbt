package com.dietrichpaul.mcnbt.primitive;

import com.dietrichpaul.mcnbt.NBTNumberPrimitive;
import com.dietrichpaul.mcnbt.NBTTagType;

/**
 * Represents an NBT tag containing a 64-bit signed integer.
 */
public class NBTLong implements NBTNumberPrimitive<Long> {

    private long value;

    private NBTLong(long value) {
        this.value = value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public static NBTLong of(long value) {
        return new NBTLong(value);
    }

    @Override
    public Long getPrimitiveType() {
        return value;
    }

    @Override
    public long asLong() {
        return value;
    }

    @Override
    public NBTTagType getTagType() {
        return NBTTagType.LONG;
    }
}
