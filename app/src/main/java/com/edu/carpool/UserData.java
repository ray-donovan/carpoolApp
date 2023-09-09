package com.edu.carpool;

public class UserData {
    private String name;
    private String phone;
    private String relationship;

    public UserData(String name, String phone, String relationship) {
        this.name = name;
        this.phone = phone;
        this.relationship = relationship;

    }
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getRelationship() {
        return relationship;
    }
}
