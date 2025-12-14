package com.dietrichpaul.mcnbt;

public interface NBTNumberPrimitive<T extends Number> extends NBTPrimitive<T> {

    @Override
    default double asDouble() {
        return getPrimitiveType().doubleValue();
    }

    @Override
    default String asString() {
        return getPrimitiveType().toString();
    }

    @Override
    default boolean asBoolean() {
        return getPrimitiveType().intValue() != 0;
    }

    @Override
    default byte asByte() {
        return getPrimitiveType().byteValue();
    }

    @Override
    default short asShort() {
        return getPrimitiveType().shortValue();
    }

    @Override
    default int asInt() {
        return getPrimitiveType().intValue();
    }

    @Override
    default long asLong() {
        return getPrimitiveType().longValue();
    }

    @Override
    default float asFloat() {
        return getPrimitiveType().floatValue();
    }
}
