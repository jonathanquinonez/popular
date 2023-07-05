package com.popular.android.mibanco.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Class that represents Notification Center response
 */

public class NotificationCenter implements Serializable {

    private static final long serialVersionUID = 1;

    @SerializedName("content")
    @Expose
    private Content content;

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public class Content {

        private String newNotifications;

        public void setNewNotifications(String newNotifications) {
            this.newNotifications = newNotifications;
        }

        public String getNewNotifications() {
            return newNotifications;
        }
    }
}
