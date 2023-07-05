package com.popular.android.mibanco.ws.response;

import java.io.Serializable;

public class AthmEnrollInfo extends AthmResponse implements Serializable {

    private static final long serialVersionUID = 4045275829594851766L;
    private AthmEnrollInfoContent content;

    public boolean isQualified() {
        return (content != null && content.qualified);
    }

    public void setQualified(boolean qualified) {
        content.qualified = qualified;
    }

    protected class AthmEnrollInfoContent implements Serializable {

        private static final long serialVersionUID = 915314688461855245L;
        private boolean qualified;
    }
}
