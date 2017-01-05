package com.example.phompang.thermalfeedback.services.Receiver;

/**
 * Created by phompang on 1/5/2017 AD.
 */

public class ReceiverManager {
    private int thermal_warning;
    private int delay_warning;

    public ReceiverManager() {
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
}
