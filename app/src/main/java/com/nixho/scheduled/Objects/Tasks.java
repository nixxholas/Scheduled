package com.nixho.scheduled.Objects;

/**
 * Created by nixho on 28-Sep-16.
 */

public class Tasks {
    private String UserId;
    private String UserName;
    private String TaskName;
    private String TaskDescription;
    private String ImageUrl;

    public Tasks(String userId, String usernName, String taskName, String taskDescription) {
        UserId = userId;
        UserName = usernName;
        TaskName = taskName;
        TaskDescription = taskDescription;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public String getTaskDescription() {
        return TaskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        TaskDescription = taskDescription;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
