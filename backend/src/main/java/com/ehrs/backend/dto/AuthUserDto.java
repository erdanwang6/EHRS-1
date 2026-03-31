package com.ehrs.backend.dto;

import java.util.List;

public class AuthUserDto {

    private final String userId;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String displayName;
    private final List<String> roles;
    private final String primaryRole;
    private final String homeRoute;
    private final String doctorEmployeeId;
    private final String nurseId;
    private final String nurseEmployeeId;

    public AuthUserDto(
            String userId,
            String username,
            String firstName,
            String lastName,
            String displayName,
            List<String> roles,
            String primaryRole,
            String homeRoute,
            String doctorEmployeeId,
            String nurseId,
            String nurseEmployeeId
    ) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.roles = roles;
        this.primaryRole = primaryRole;
        this.homeRoute = homeRoute;
        this.doctorEmployeeId = doctorEmployeeId;
        this.nurseId = nurseId;
        this.nurseEmployeeId = nurseEmployeeId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getPrimaryRole() {
        return primaryRole;
    }

    public String getHomeRoute() {
        return homeRoute;
    }

    public String getDoctorEmployeeId() {
        return doctorEmployeeId;
    }

    public String getNurseId() {
        return nurseId;
    }

    public String getNurseEmployeeId() {
        return nurseEmployeeId;
    }
}
