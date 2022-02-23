package com.luitmed.prashantimedicos;

public class Address {
    String name, phone, address, pinCode, addressID;

    public Address(String name, String phone, String address, String pinCode, String addressID) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.pinCode = pinCode;
        this.addressID = addressID;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getAddressID() {
        return addressID;
    }

    public void setAddressID(String addressID) {
        this.addressID = addressID;
    }
}

