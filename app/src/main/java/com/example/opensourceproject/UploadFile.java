package com.example.opensourceproject;

public class UploadFile {
    public String user;
    public String content;
    public String andPolicy;
    public String orPolicy;

    public UploadFile(String user, String content, String andPolicy, String orPolicy) {
        this.user = user;
        this.content = content;
        this.andPolicy = andPolicy;
        this.orPolicy = orPolicy;
    }

    public String getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public String getAndPolicy() {
        return andPolicy;
    }

    public String getOrPolicy() {
        return orPolicy;
    }
}
