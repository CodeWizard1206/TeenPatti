package com.pacss.teenPatti.dataHandler;

public class UserHandler {
    private String UserName;
    private long TotalTokens;
    private String UserType;

    private static UserHandler Object = new UserHandler();

    private UserHandler() {
    }

    public static UserHandler UserHandlerReference() {
        return Object;
    }

    public void setUser(String userName, long totalTokens, String userType) {
        UserName = userName;
        TotalTokens = totalTokens;
        UserType = userType;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public long getTotalTokens() {
        return TotalTokens;
    }

    public void setTotalTokens(long totalTokens) {
        TotalTokens = totalTokens;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }
}
