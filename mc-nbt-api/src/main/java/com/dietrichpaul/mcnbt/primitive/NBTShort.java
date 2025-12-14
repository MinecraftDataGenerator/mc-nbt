package com.dietrichpaul.mcnbt.primitive;

import com.dietrichpaul.mcnbt.NBTNumberPrimitive;
import com.dietrichpaul.mcnbt.NBTTagType;

/**
 * Represents an NBT tag containing a 16-bit signed integer.
 */
public class NBTShort implements NBTNumberPrimitive<Short> {

    private short value;

    private NBTShort(short value) {
        this.value = value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    public static NBTShort of(short value) {
        return new NBTShort(value);
    }

    @Override
    public Short getPrimitiveType() {
        return value;
    }

    @Override
    public short asShort() {
        return value;
    }

    @Override
    public NBTTagType getTagType() {
        return NBTTagType.SHORT;
    }
}