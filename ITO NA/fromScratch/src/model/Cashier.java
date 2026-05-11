package model;

public class Cashier extends User {
    private static final long serialVersionUID = 1L;

    public Cashier(String username, String password, String fullName) {
        super(username, password, fullName, "Cashier");
    }
}