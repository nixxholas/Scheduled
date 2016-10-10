package com.nixho.scheduled.Objects;

import android.icu.util.Calendar;

import java.util.Date;

/**
 * Created by nixho on 28-Sep-16.
 */

public class Tasks {
    private String UserId;
    private String UserName;
    private String TaskName;
    private String TaskDescription;
    private String TaskDeadline;
    private String ImageUrl;

    public Tasks() {

    }

    public Tasks(String userId, String usernName, String taskName, String taskDescription, String taskDeadline) {
        UserId = userId;
        UserName = usernName;
        TaskName = taskName;
        TaskDescription = taskDescription;
        TaskDeadline = taskDeadline;
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

    public String getTaskDeadline() {
        return TaskDeadline;
    }

    public void setTaskDeadline(String taskDeadline) {
        TaskDeadline = taskDeadline;
    }
}
