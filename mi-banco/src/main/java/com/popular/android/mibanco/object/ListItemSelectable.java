package com.popular.android.mibanco.object;

public class ListItemSelectable {
    private String comment;

    private String content;
    private int id;
    protected boolean selected;
    private String title;

    public ListItemSelectable() {
    }

    public ListItemSelectable(final int id, final String title) {
        this(id, title, "", "");
    }

    public ListItemSelectable(final int id, final String title, final String content) {
        this(id, title, content, "");
    }

    public ListItemSelectable(final int id, final String title, final String content, final boolean selected) {
        this(id, title, content, "");
        this.selected = selected;
    }

    public ListItemSelectable(final int id, final String title, final String content, final String comment) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.comment = comment;
        selected = false;
    }

    public ListItemSelectable(final String title, final String content) {
        this(-1, title, content, "");
    }

    public ListItemSelectable(final String title, final String content, final boolean selected) {
        this(-1, title, content, "");
        this.selected = selected;
    }

    public ListItemSelectable(final String title, final String content, final String comment) {
        this(-1, title, content, comment);
    }

    public String getComment() {
        return comment;
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSelected() {
        return selected;
    }

}
