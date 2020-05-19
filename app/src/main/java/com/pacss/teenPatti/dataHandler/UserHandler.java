package com.pacss.teenPatti.dataHandler;

public class UserHandler {
    private String UserName;
    private int TotalTokens;
    private String UserType;
    private int TotalGamesPlayed;
    private int TotalLosses;
    private int TotalWins;

    private static UserHandler Object = new UserHandler();

    private UserHandler() {
    }

    public static UserHandler UserHandlerReference() {
        return Object;
    }

    public void setUser(String userName, int totalTokens, String userType, int totalGamesPlayed, int totalLosses, int totalWins) {
        UserName = userName;
        TotalTokens = totalTokens;
        UserType = userType;
        TotalGamesPlayed = totalGamesPlayed;
        TotalLosses = totalLosses;
        TotalWins = totalWins;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getTotalTokens() {
        return TotalTokens;
    }

    public void setTotalTokens(int totalTokens) {
        TotalTokens = totalTokens;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }

    public int getTotalGamesPlayed() {
        return TotalGamesPlayed;
    }

    public void setTotalGamesPlayed(int totalGamesPlayed) {
        TotalGamesPlayed = totalGamesPlayed;
    }

    public int getTotalLosses() {
        return TotalLosses;
    }

    public void setTotalLosses(int totalLosses) {
        TotalLosses = totalLosses;
    }

    public int getTotalWins() {
        return TotalWins;
    }

    public void setTotalWins(int totalWins) {
        TotalWins = totalWins;
    }
}
