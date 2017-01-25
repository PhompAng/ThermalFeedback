package com.example.phompang.thermalfeedback.services.Receiver;

/**
 * Created by phompang on 1/5/2017 AD.
 */

public class ReceiverManager {
    private int thermal_warning;
    private int delay_warning;
    private boolean isPause = false;
    private String uid;
    private int pulseWidth;

    private static ReceiverManager sReceiverManager;

    private ReceiverManager() {
    }

    public static ReceiverManager getInstance() {
        if (sReceiverManager == null) {
            sReceiverManager = new ReceiverManager();
        }
        return sReceiverManager;
    }

    public Integer getThermal_warning() {
        return thermal_warning;
    }

    public void setThermal_warning(Integer thermal_warning) {
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
}
