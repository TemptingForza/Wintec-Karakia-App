package com.group3.karakiaapp;

public interface Action<T> {
    public void invoke(T value);
}
