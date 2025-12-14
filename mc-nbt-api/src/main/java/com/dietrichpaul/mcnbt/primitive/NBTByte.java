package com.dietrichpaul.mcnbt.primitive;

import com.dietrichpaul.mcnbt.NBTNumberPrimitive;
import com.dietrichpaul.mcnbt.NBTTagType;

/**
 * Represents an NBT tag containing an 8-bit signed integer.
 */
public class NBTByte implements NBTNumberPrimitive<Byte> {

    private byte value;

    private NBTByte(byte value) {
        this.value = value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    public static NBTByte of(byte value) {
        return new NBTByte(value);
    }

    @Override
    public Byte getPrimitiveType() {
        return value;
    }

    @Override
    public byte asByte() {
        return value;
    }

    @Override
    public NBTTagType getTagType() {
        return NBTTagType.BYTE;
    }

}