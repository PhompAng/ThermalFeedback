package com.example.phompang.thermalfeedback.model;

/**
 * Created by phompang on 12/29/2016 AD.
 */

public class Contact {
    private String key;
    private String name;
    private String phone;

    public Contact() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
