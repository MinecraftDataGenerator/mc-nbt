package com.dietrichpaul.mcnbt.primitive;

import com.dietrichpaul.mcnbt.NBTNumberPrimitive;
import com.dietrichpaul.mcnbt.NBTTagType;

/**
 * Represents an NBT tag containing a 64-bit floating point number.
 */
public class NBTDouble implements NBTNumberPrimitive<Double> {

    private double value;

    private NBTDouble(double value) {
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public static NBTDouble of(double value) {
        return new NBTDouble(value);
    }

    @Override
    public Double getPrimitiveType() {
        return value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public NBTTagType getTagType() {
        return NBTTagType.DOUBLE;
    }
}