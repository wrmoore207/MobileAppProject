package com.example.scheduleapp;

import com.google.firebase.Timestamp;

public class ShiftModel {

    private String shiftID;
    private String Employee;
    private Timestamp StartTime;
    private Timestamp EndTime;
    private String Role;
    private boolean isOpen;

    // Constructors
    public ShiftModel(String employee, String role) {
        Employee = employee;
        Role = role;
    }

    public ShiftModel(String id, String employee, Timestamp startTime, Timestamp endTime, String role) {
        this.shiftID = id;
        Employee = employee;
        StartTime = startTime;
        EndTime = endTime;
        Role = role;
    }
    public ShiftModel(String employee, Timestamp startTime, Timestamp endTime, String role) {
        Employee = employee;
        StartTime = startTime;
        EndTime = endTime;
        Role = role;
    }

    public ShiftModel() {
    }

    public String getId() {
        return shiftID;
    }

    public void setId(String SID) {
        this.shiftID = SID;
    }

    public String getEmployee() {
        return Employee;
    }

    public void setEmployee(String employee) {
        Employee = employee;
    }

    public Timestamp getStartTimeStamp() {
        return StartTime;
    }

    public void setStartTimeStamp(Timestamp startTime) {
        StartTime = startTime;
    }

    public Timestamp getEndTimeStamp() {
        return EndTime;
    }

    public void setEndTimeStamp(Timestamp endTime) {
        EndTime = endTime;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public void setOpen(Boolean open) {
        this.isOpen = open;
    }
}