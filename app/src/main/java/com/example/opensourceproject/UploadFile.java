package com.example.opensourceproject;

public class UploadFile {
    public String user;
    public String content;

    public UploadFile(String user, String content) {
        this.user = user;
        this.content = content;
    }

    public String getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }
}
