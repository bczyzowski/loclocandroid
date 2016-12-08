package com.bczyzowski.locator.model;

/**
 * Created by bczyz on 17.10.2016.
 */

public class UserAuthFromMobileDev {
    private String email;
    private String password;

    public UserAuthFromMobileDev(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
