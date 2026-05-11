package model;

import java.io.Serializable;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String fullName;
    private String role;

    public User(String username, String password, String fullName, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public Boolean validatePassword(String password) {
        if (this.password.equals(password)) return true;
        return false;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }
}