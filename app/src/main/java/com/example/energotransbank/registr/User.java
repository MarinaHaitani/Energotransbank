package com.example.energotransbank.registr;
public class User {
    public String firstName, lastName, patronymic, dob, gender, phoneNumber;

    public User(String firstName, String lastName, String patronymic, String dob, String gender, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.dob = dob;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
    }
}

