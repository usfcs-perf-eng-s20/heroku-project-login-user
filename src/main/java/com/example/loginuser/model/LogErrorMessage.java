package com.example.loginuser.model;

public class LogErrorMessage {
    String serviceName;
    int runTime;
    int error;
    String msg;
    public LogErrorMessage(String serviceName, int runTime, int error, String msg) {
        this.serviceName = serviceName;
        this.runTime = runTime;
        this.error = error;
        this.msg = msg;
    }

    @Override public String toString() {
        return "serviceName=" + this.serviceName + ", runTime=" + this.runTime + ", error=" + this.error + ", msg=" + this.msg;
    }
}
