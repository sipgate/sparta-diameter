package com.sipgate.sparta.diameter.core;

public interface Selfable<T extends Selfable<T>> {

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
