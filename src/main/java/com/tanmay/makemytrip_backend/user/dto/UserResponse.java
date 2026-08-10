package com.tanmay.makemytrip_backend.user.dto;

import com.tanmay.makemytrip_backend.user.entity.UserRole;

public class UserResponse {

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;
    private final UserRole role;
    private final Boolean active;

    public UserResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            UserRole role,
            Boolean active) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.active = active;
    }


    // Getters

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserRole getRole() {
        return role;
    }

    public Boolean getActive() {
        return active;
    }

}