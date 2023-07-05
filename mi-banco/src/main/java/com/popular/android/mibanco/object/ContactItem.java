package com.popular.android.mibanco.object;

public class ContactItem {

    public enum ContactType {
        CONTACT_TYPE_EMAIL, CONTACT_TYPE_HEADER, CONTACT_TYPE_OTHER, CONTACT_TYPE_PHONE, CONTACT_TYPE_WEB
    }

    private String content;
    private String description;
    private ContactType type;

    public ContactItem(final String description, final String content, final ContactType type) {
        this.description = description;
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public ContactType getType() {
        return type;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setType(final ContactType type) {
        this.type = type;
    }
}
