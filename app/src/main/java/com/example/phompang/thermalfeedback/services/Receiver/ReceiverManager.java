package com.example.phompang.thermalfeedback.services.Receiver;

import com.example.phompang.thermalfeedback.app.FirebaseUtils;
import com.example.phompang.thermalfeedback.model.Notification;

/**
 * Created by phompang on 1/5/2017 AD.
 */

public class ReceiverManager {
    private int thermal_warning;
    private int delay_warning;
    private boolean isPause = false;
    private String uid;
    private int day;
    private int pulseWidth;

    private int count;
    private int current_thermal_warning;
    private boolean hold;

    private static ReceiverManager sReceiverManager;

    private ReceiverManager() {
        count = 0;
        current_thermal_warning = 0;
        setHold(false);
    }

    public static ReceiverManager getInstance() {
        if (sReceiverManager == null) {
            sReceiverManager = new ReceiverManager();
        }
        return sReceiverManager;
    }

    public void pushNotification(Notification notification) {
        FirebaseUtils.addNotification(uid, day, notification);
    }

    public void responseNotification(String type, long responseTime) {
        FirebaseUtils.responseNotification(uid, day, type, responseTime);
    }

    public void endCallNotification(long endTime) {
        FirebaseUtils.endCallNotification(uid, day, "Incoming Call", endTime);
    }

    private void checkDuplicate(int thermal_warning) {
        if (priority(thermal_warning) > priority(this.current_thermal_warning)) {
            count = 1;
            current_thermal_warning = thermal_warning;
        } else if (thermal_warning == this.current_thermal_warning){
            count++;
        } else if (thermal_warning == 0){
            count = 0;
            current_thermal_warning = 0;
        }
    }

    private void checkHold() {
        switch (current_thermal_warning) {
            case 1: //very hot
            case 2: //hoy
                if (count >= 2) {
                    this.hold = true;
                }
                break;
            case 3: //cold
            case 4: //very cold
                if (count >= 10) {
                    this.hold = true;
                }
                break;
            default:
                this.hold = false;
                break;
        }
    }

    public Integer getThermal_warning() {
        if (isHold()) {
            return current_thermal_warning;
        }
        return thermal_warning;
    }

    public void setThermal_warning(Integer thermal_warning) {
        checkDuplicate(thermal_warning);
        checkHold();
        this.thermal_warning = thermal_warning;
    }

    public Integer getDelay_warning() {
        return delay_warning;
    }

    public void setDelay_warning(Integer delay_warning) {
        this.delay_warning = delay_warning;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getPulseWidth() {
        return pulseWidth;
    }

    public void setPulseWidth(int pulseWidth) {
        this.pulseWidth = pulseWidth;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    private int priority(int thermal_warning) {
        switch (thermal_warning) {
            case 1: //very hot
                return 4;
            case 2: //hot
                return 2;
            case 3: //cold
                return 1;
            case 4: //very cold
                return 3;
            default:
                return 0;
        }
    }

    public boolean isHold() {
        return hold;
    }

    public void setHold(boolean hold) {
        this.hold = hold;
    }
}
