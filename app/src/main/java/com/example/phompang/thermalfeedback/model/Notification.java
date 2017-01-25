package com.example.phompang.thermalfeedback.model;

/**
 * Created by phompang on 12/28/2016 AD.
 */

public class Notification {
    private int attempt;
    private boolean isReal;
    private String type;
    private String phone;
    private int stimuli;
    private boolean isContact;
    private boolean isThermal;
    private boolean isVibrate;
    private long startTime;
    private long responseTime;
    private long endTime;

    public Notification() {
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    public boolean isReal() {
        return isReal;
    }

    public void setIsReal(boolean real) {
        setReal(real);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getStimuli() {
        return stimuli;
    }

    public void setStimuli(int stimuli) {
        this.stimuli = stimuli;
    }

    public boolean isContact() {
        return isContact;
    }

    public void setIsContact(boolean contact) {
        setContact(contact);
    }

    public boolean isThermal() {
        return isThermal;
    }

    public void setIsThermal(boolean thermal) {
        setThermal(thermal);
    }

    public boolean isVibrate() {
        return isVibrate;
    }

    public void setIsVibrate(boolean vibrate) {
        setVibrate(vibrate);
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setReal(boolean real) {
        isReal = real;
    }

    public void setContact(boolean contact) {
        isContact = contact;
    }

    public void setThermal(boolean thermal) {
        isThermal = thermal;
    }

    public void setVibrate(boolean vibrate) {
        isVibrate = vibrate;
    }

    @Override
    public String toString() {
        return getType() + ":" + getStartTime();
    }
}
