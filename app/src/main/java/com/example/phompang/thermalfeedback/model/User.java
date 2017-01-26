package com.example.phompang.thermalfeedback.model;

import java.util.Map;

/**
 * Created by phompang on 12/29/2016 AD.
 */

public class User {
    private String uid;
    private int numberOfSession;
    private String name;
    private String surname;
    private int age;
    private String gender;
    private Map<String, Map<String, Notification>> notificationsList;

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getNumberOfSession() {
        return numberOfSession;
    }

    public void setNumberOfSession(int numberOfSession) {
        this.numberOfSession = numberOfSession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Map<String, Map<String, Notification>> getNotificationsList() {
        return notificationsList;
    }

    public void setNotificationsList(Map<String, Map<String, Notification>> notificationsList) {
        this.notificationsList = notificationsList;
    }
}
