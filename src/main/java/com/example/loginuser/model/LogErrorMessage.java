package com.example.loginuser.model;

public class LogErrorMessage {
    String serviceName;
    String path;
    int runTime;
    int statusCode;
    String msg;
    public LogErrorMessage(String serviceName, String path, int runTime, int statusCode, String msg) {
        this.serviceName = serviceName;
        this.path = path;
        this.runTime = runTime;
        this.statusCode = statusCode;
        this.msg = msg;
    }

    @Override public String toString() {
        return "serviceName=" + this.serviceName + ", path=" + this.path + ", runTime=" + this.runTime + ", statusCode=" + this.statusCode + ", msg=\"" + this.msg + "\"";
    }
}
