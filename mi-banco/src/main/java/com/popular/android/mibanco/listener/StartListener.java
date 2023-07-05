package com.popular.android.mibanco.listener;

import com.popular.android.mibanco.model.LoginGet;

/**
 * Interface to start login listener
 */
public interface StartListener extends TaskListener {
    void savedData(LoginGet loginData);
}
