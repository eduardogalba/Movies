package com.example.movies.notifications;

public class NotificationJob {

    private int notifID;
    private int groupID;
    private String groupName;
    private String notifCode;
    private String title;
    private String msg;
    private int imageId;
    private boolean selected;


    public NotificationJob (int notifID) {
        this.notifID = notifID;
    }

    public NotificationJob (String notifCode, String title, String msg, int imageUri) {
        this.notifCode = notifCode;
        this.title = title;
        this.msg = msg;
        this.imageId = imageUri;
        this.selected = false;
    }

    public String getTitle() {
        return title;
    }

    public String getMsg() {
        return msg;
    }

    public int getImageId() {
        return imageId;
    }

    public String getNotifCode() {
        return notifCode;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public int getNotifID() {
        return notifID;
    }

    public void setNotifID(int notifID) {
        this.notifID = notifID;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
