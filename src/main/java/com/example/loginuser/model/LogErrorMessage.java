package com.example.loginuser.model;

public class LogErrorMessage {
    String serviceName;
    int runTime;
    int statusCode;
    String msg;
    public LogErrorMessage(String serviceName, int runTime, int statusCode, String msg) {
        this.serviceName = serviceName;
        this.runTime = runTime;
        this.statusCode = statusCode;
        this.msg = msg;
    }

    @Override public String toString() {
        return "serviceName=" + this.serviceName + ", runTime=" + this.runTime + ", status code=" + this.statusCode + ", msg=" + this.msg;
    }
}
