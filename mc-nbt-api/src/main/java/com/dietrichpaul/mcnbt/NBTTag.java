package com.dietrichpaul.mcnbt;

public interface NBTTag<T> {

    NBTTagType getTagType();

    default NBTIterable<T> asIterable() {
        throw new IllegalArgumentException("Cannot parse " + getTagType() + " as iterable");
    }

    default NBTCompound asCompound() {
        throw new IllegalArgumentException("Cannot parse " + getTagType() + " as compound");
    }

    default NBTList<?> asList() {
        throw new IllegalArgumentException("Cannot parse " + getTagType() + " as list");
    }
}
