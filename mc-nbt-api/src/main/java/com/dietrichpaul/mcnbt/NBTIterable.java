package com.dietrichpaul.mcnbt;

import java.util.Iterator;
import java.util.Spliterator;

public interface NBTIterable<T> extends NBTTag<T>, Iterable<T> {

    @Override
    Iterator<T> iterator();

    @Override
    Spliterator<T> spliterator();

    @Override
    default NBTIterable<T> asIterable() {
        return this;
    }
}
