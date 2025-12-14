package com.dietrichpaul.mcnbt;

/**
 * Represents an NBT tag with a primitive (non-collection) value.
 * <p>
 * Each concrete primitive tag must implement conversions into all standard
 * numeric types explicitly. The asX() methods are abstract to force
 * type-specific behavior.
 *
 * @param <T> the underlying primitive value type
 */
public interface NBTPrimitive<T> extends NBTTag<T> {

    /**
     *
     * @return the primitive value
     */
    T getPrimitiveType();

    /**
     *
     * @return the value as a String
     */
    String asString();

    /**
     * @return the value as a boolean
     */
    boolean asBoolean();

    /**
     *
     * @return the value as a byte
     */
    byte asByte();

    /**
     *
     * @return the value as a short
     */
    short asShort();

    /**
     *
     * @return the value as an int
     */
    int asInt();

    /**
     *
     * @return the value as a long
     */
    long asLong();

    /**
     *
     * @return the value as a float
     */
    float asFloat();

    /**
     *
     * @return the value as a double
     */
    double asDouble();
}
