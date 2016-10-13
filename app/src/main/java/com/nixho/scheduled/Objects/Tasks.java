package com.nixho.scheduled.Objects;

import android.icu.util.Calendar;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by nixho on 28-Sep-16.
 *
 * Why we need to have a serializable object.
 * http://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents
 */

public class Tasks implements Serializable{
    private String UniqueId;
    private String UserName;
    private String TaskName;
    private String TaskDescription;
    private String TaskDeadline;
    private String ImageUrl;

    public Tasks() {

    }

    public Tasks(String userName, String taskName, String taskDescription, String taskDeadline) {
        UserName = userName;
        TaskName = taskName;
        TaskDescription = taskDescription;
        TaskDeadline = taskDeadline;
    }

    public Tasks(String userName, String taskName, String taskDescription, String taskDeadline, String imageUrl) {
        UserName = userName;
        TaskName = taskName;
        TaskDescription = taskDescription;
        TaskDeadline = taskDeadline;
        ImageUrl = imageUrl;
    }

    public Tasks(String UID, String userName, String taskName, String taskDescription, String taskDeadline, String imageUrl) {
        UniqueId = UID;
        UserName = userName;
        TaskName = taskName;
        TaskDescription = taskDescription;
        TaskDeadline = taskDeadline;
        ImageUrl = imageUrl;
    }

    public String getUniqueId() {
        return UniqueId;
    }

    public void setUniqueId(String uniqueId) {
        UniqueId = uniqueId;
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
