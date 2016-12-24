package com.santiagogil.takestock.util;

/**
 * Created by digitalhouse on 24/12/16.
 */
public interface ResultListener<T> {
    void finish(T result);
}