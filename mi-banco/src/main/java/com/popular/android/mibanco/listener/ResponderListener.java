package com.popular.android.mibanco.listener;

/**
 * Interface for responder
 * @param <T>
 */
public interface ResponderListener<T> extends SessionListener {

    void responder(String responderName, T data);
}
