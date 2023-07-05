package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AthmSSOInfo extends AthmResponse implements Serializable {
    private static final long serialVersionUID = 8232852529807190779L;
    private AthmTokenContent content;

    public String getToken() {
        return content.token;
    }

    public void setToken(String token) {
        content.token = token;
    }

    public boolean isSsoFlagEnabled() { return  content.flag; }

    public void setSsoFlagEnabled(boolean ssoFlagEnabled) { content.flag = ssoFlagEnabled; }

    public boolean isSsoBound() { return  content.bound; }

    public void setSsoBound(boolean ssoBound) { content.bound = ssoBound; }

    public boolean isUnBoundSuccess() { return  content.success; }

    public void setUnBoundSuccess(boolean ssoUnBoundSuccess) { content.success = ssoUnBoundSuccess; }

    public boolean isSSODowntime() {
        return content.isDowntime || this.isDowntime();
    }

    public void setSSODowntime(boolean downtime) {
        content.isDowntime = downtime;
    }

    protected class AthmTokenContent implements Serializable {

        private static final long serialVersionUID = -1954375633515191013L;
        @SerializedName("token")
        private String token;
        @SerializedName("flag")
        private boolean flag;
        @SerializedName("bound")
        private boolean bound;
        @SerializedName("success")
        private boolean success;
        @SerializedName("downtime")
        private boolean isDowntime;
    }
}
