package com.pacss.teenPatti.dataHandler;

public class UserHandler {
    private String userName;
    private String userID;
    private int chipAmount;
    private boolean isGuest =  false;
    private boolean isAdmin = false;
    private boolean isPlayer = false;
    public final static int Admin = 0;

    private static UserHandler Object = new UserHandler();

    public UserHandler() {}

    public static UserHandler getUserHandlerReference() {
        return Object;
    }

    public void setUser(final String userName, final String userID, final String userType) {
        this.userName = userName;
        this.userID = userID;
        if (userType.equals("Guest")) {
            isGuest = true;
        } else if (userType.equals("Admin")) {
            isAdmin = true;
        } else {
            isPlayer = true;
        }
    }

    public int getUserType() {
        if (isAdmin) {
            return Admin;
        } else {
            return 1;
        }
    }

    public void setChipAmount(int chipAmount) {
        this.chipAmount = chipAmount;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserID() {
        return userID;
    }

    public int getChipAmount() {
        return chipAmount;
    }
}
