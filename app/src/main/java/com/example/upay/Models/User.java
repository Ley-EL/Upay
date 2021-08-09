package com.example.upay.Models;

public class User {

    private String name;
    private String infoName;
    private String infoNumber;
    private String infoAddress;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfoName() {
        return infoName;
    }

    public void setInfoName(String infoName) {
        this.infoName = infoName;
    }

    public String getInfoNumber() {
        return infoNumber;
    }

    public void setInfoNumber(String infoNumber) {
        this.infoNumber = infoNumber;
    }

    public String getInfoAddress() {
        return infoAddress;
    }

    public void setInfoAddress(String infoAddress) {
        this.infoAddress = infoAddress;
    }
}
