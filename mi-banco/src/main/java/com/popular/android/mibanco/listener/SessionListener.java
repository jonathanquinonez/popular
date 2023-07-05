package com.popular.android.mibanco.listener;

/**
 * Interface for location listener
 */
public interface SessionListener extends TaskListener {
    void sessionHasExpired();
}
