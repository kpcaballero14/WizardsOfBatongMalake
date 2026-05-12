package model;

public class Manager extends User {
    private static final long serialVersionUID = 1L;

    public Manager(String username, String password, String fullName) {
        super(username, password, fullName, "Manager");
    }
}