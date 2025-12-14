package com.dietrichpaul.mcnbt.primitive;

import com.dietrichpaul.mcnbt.NBTNumberPrimitive;
import com.dietrichpaul.mcnbt.NBTTagType;

/**
 * Represents an NBT tag containing a 32-bit floating point number.
 */
public class NBTFloat implements NBTNumberPrimitive<Float> {

    private float value;

    private NBTFloat(float value) {
        this.value = value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public static NBTFloat of(float value) {
        return new NBTFloat(value);
    }

    @Override
    public Float getPrimitiveType() {
        return value;
    }


    @Override
    public float asFloat() {
        return value;
    }

    @Override
    public NBTTagType getTagType() {
        return NBTTagType.FLOAT;
    }
}