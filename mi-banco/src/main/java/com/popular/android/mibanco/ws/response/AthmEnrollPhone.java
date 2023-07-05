package com.popular.android.mibanco.ws.response;

import com.popular.android.mibanco.model.AthmPhoneProvider;

import java.io.Serializable;
import java.util.ArrayList;

public class AthmEnrollPhone extends AthmResponse implements Serializable {

    private static final long serialVersionUID = 5775403284477796503L;
    private AthmEnrollPhoneContent content;

    public ArrayList<AthmPhoneProvider> getProviders() {

        return content.providers;
    }

    public void setProviders(ArrayList<AthmPhoneProvider> providers) {
        content.providers = providers;
    }

    protected class AthmEnrollPhoneContent implements Serializable {

        private static final long serialVersionUID = 5046748523988101113L;
        private ArrayList<AthmPhoneProvider> providers;
    }
}
