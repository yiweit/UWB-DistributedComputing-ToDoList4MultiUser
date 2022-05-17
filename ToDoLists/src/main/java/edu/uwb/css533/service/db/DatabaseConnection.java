package edu.uwb.css533.service.db;

import javax.xml.transform.Result;
import java.sql.*;

public class DatabaseConnection {

    private String url = "jdbc:postgresql://localhost:5432/todolistdb";
    private String username = "postgres";
    private String password = "password";
    private int connectionTries = 3;

    Connection connection;

    public DatabaseConnection() {
    }

    public void connect() {
        Connection result = null;
        try {
            result = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return;
        }

        connection = result;
        System.out.println("Connected to " + url);
    }

    public String addUser(String username, String password) {
        if(isConnected()) {
            String sql = "INSERT INTO USERS_INFO (username, password) VALUES (?, ?);";
            try {
                PreparedStatement statement = connection.prepareStatement(sql);

                statement.setString(1, username);
                statement.setString(2, password);

                int rows = statement.executeUpdate();
                return "Successfully add user: " + username;
            } catch (SQLException e) {
//                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        } else {

            return "Error: Unable to connect " + url;
        }


    }

    private String usernameOccurence(String username) {
        if (isConnected()) {
            String sql = "SELECT * FROM USERS_INFO WHERE USERNAME=?;";
            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, username);

                int rows = statement.executeUpdate();
                return Integer.toString(rows);
            } catch (SQLException e) {
                return "Error: " + e.getMessage();
            }
        } else {
            return "Error: Unable to connect " + url;
        }
    }

    public String getAllUsernames(){
        if (isConnected()) {

            String sql = "SELECT USERNAME FROM USERS_INFO ORDER BY USERNAME;";
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                String res = "";
                while (rs.next()) {
                    res += rs.getString("username") + "\n";
                }
                return res;
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        } else {

            return "Error: Unable to connect " + url;
        }
    }

    /**
     * 1. Search (username, password) pair in Users_info table and count the occurance
     * @param username
     * @param password
     * @return successful logged in message if count = 1, Error message otherwise
     */
    public String logIn(String username, String password) {
        if (isConnected()) {
            String sql = "SELECT COUNT(*) FROM USERS_INFO WHERE USERNAME=? AND PASSWORD=?;";
            try {
                PreparedStatement statement = connection.prepareStatement(sql);

                statement.setString(1, username);
                statement.setString(2, password);

                ResultSet rs = statement.executeQuery();
                int cnt = 0;
                while (rs.next()) {
                    cnt = rs.getInt(1);

                }
                if (cnt > 0) {
                    return "Successfully logged in user: " + username;
                }
                return "Error: username or password not matching.";

            } catch (SQLException e) {
//                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        } else {

            return "Error: Unable to connect " + url;
        }

    }

    public String resetPassword (String username, String oldPassword, String newPassword) {
        if (isConnected()) {
            String res;
//            // step 1 check username existence
//            res = usernameOccurence(username);
//            System.out.println("check occurence: " +
//                    /*String.format("%s is shown in db %s time(s)", username,res)*/ res);
            // step 2 check (username, oldPassword) existence in Users_info table
            res = logIn(username, oldPassword);
            System.out.println("login: " + res);
            if (res.contains("Error")) {
                return String.format("Error: username (%s) and password do not match.", username);
            }
            String sql = "UPDATE Users_info SET password=? WHERE username=? AND password=?;";
            try{
                PreparedStatement statement = connection.prepareStatement(sql);

                statement.setString(1, newPassword);
                statement.setString(2, username);
                statement.setString(3, oldPassword);

                int rows = statement.executeUpdate();
                return String.format("Password reseted sucessfully for username (%s)", username);
            } catch (SQLException e) {
                return "Error: " + e.getMessage();
            }

        } else {
            return "Error: Unable to connect " + url;
        }
    }

    private Boolean isConnected() {
        int cnt = connectionTries;
        while (connection == null & cnt > 0) {
            connect();
            cnt--;
        }
        if (connection == null) {

            return false;
        }
        return true;
    }

}
