package com.dietrichpaul.mcnbt.primitive;

import com.dietrichpaul.mcnbt.NBTNumberPrimitive;
import com.dietrichpaul.mcnbt.NBTTagType;

/**
 * Represents an NBT tag containing a 32-bit signed integer.
 */
public class NBTInt implements NBTNumberPrimitive<Integer> {

    private int value;

    private NBTInt(int value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static NBTInt of(int value) {
        return new NBTInt(value);
    }

    @Override
    public Integer getPrimitiveType() {
        return value;
    }

    @Override
    public int asInt() {
        return value;
    }

    @Override
    public NBTTagType getTagType() {
        return NBTTagType.INT;
    }
}