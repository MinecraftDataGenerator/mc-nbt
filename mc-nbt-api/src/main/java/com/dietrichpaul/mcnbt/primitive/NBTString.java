package com.dietrichpaul.mcnbt.primitive;

import com.dietrichpaul.mcnbt.NBTPrimitive;
import com.dietrichpaul.mcnbt.NBTTagType;

/**
 * Represents an NBT tag containing a UTF-8 string (TAG_String).
 * <p>
 * Strings are considered primitive in NBT. Provides conversion to string
 * and boolean (non-empty check).
 */
public class NBTString implements NBTPrimitive<String> {

    private String value;

    private NBTString(String value) {
        this.value = value;
    }

    /**
     * Creates a new {@link NBTString} with the given value.
     *
     * @param value the string value
     * @return a new {@link NBTString} instance
     */
    public static NBTString of(String value) {
        return new NBTString(value);
    }

    /**
     * Updates the value of this string tag.
     *
     * @param value the new string value
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getPrimitiveType() {
        return value;
    }

    @Override
    public boolean asBoolean() {
        return value != null && !value.isEmpty();
    }

    @Override
    public byte asByte() {
        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public short asShort() {
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public int asInt() {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public long asLong() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    @Override
    public float asFloat() {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    @Override
    public double asDouble() {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public NBTTagType getTagType() {
        return NBTTagType.STRING;
    }
}
