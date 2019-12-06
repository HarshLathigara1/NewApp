package com.example.newapp;

public class Friends {

    String name;
    String status;
    String image;
    String online;

    public Friends(){

    }

    public Friends(String name,String image,String status,String online ) {
        this.name = name;
        this.image = image;
        this.status  =status;
        this.online = online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }
}
