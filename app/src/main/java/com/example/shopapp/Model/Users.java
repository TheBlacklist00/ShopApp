package com.example.shopapp.Model;

public class Users
{
    private String  name,password,username,image,adress;

    public Users()
    {

    }

    public Users(String name, String password, String username, String image, String adress) {
        this.name = name;
        this.password = password;
        this.username = username;
        this.image = image;
        this.adress = adress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }
}
