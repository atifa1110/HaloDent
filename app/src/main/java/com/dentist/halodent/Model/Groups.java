package com.dentist.halodent.Model;

public class Groups {

    private String groupId,groupTitle,groupIcon,timestamp,status,unread;

    public Groups(){

    }

    public Groups(String groupId, String groupTitle, String groupIcon, String timestamp, String status) {
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.groupIcon = groupIcon;
        this.timestamp = timestamp;
        this.status = status;
    }

    public Groups(String groupId, String groupTitle, String groupIcon, String timestamp, String status,String unread) {
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.groupIcon = groupIcon;
        this.timestamp = timestamp;
        this.status = status;
        this.unread = unread;
    }


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUnread() {
        return unread;
    }

    public void setUnread(String unread) {
        this.unread = unread;
    }
}
