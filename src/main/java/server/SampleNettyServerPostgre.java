package server;

import dbconfig.PostgreBootstrap;
import errors.InvalidInputException;
import structs.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class SampleNettyServerPostgre {
    // DATABASE QUERY METHODS ==========================================================================================
    public static User selectUser(SelectUserInput selectUserInput)
            throws ClassNotFoundException, InvalidInputException, IllegalAccessException, SQLException {
        // Connecting To Database
        Connection postgresql = PostgreBootstrap.bootstrap("jdbc:postgresql://192.168.10.68:5432/servicetesterdb", "servicetester", "123qwe");

        // Preparing Query
        String query = generateQuery("SELECT", "st_user", selectUserInput);
        PreparedStatement database = postgresql.prepareStatement(query);
        database.setInt(1, SampleNettyServerHandler.convertCharArrayToInt(selectUserInput.userNo));

        // Executing Query
        ResultSet targetUsers = database.executeQuery(); // Rows of Users

        // Reading User Info
        User user = SampleNettyServerHandler.instantiateUser(new User());
        try {
            if(targetUsers.next()) {
                readUserFromDatabase(targetUsers, user);
            }
        }
        finally {
            targetUsers.close();
            database.close();
            postgresql.close();
        }
        return user;
    }
    /* selectUserList
        Input:
            SelectUserListInput
        Output:
            Returns the list of users who match search specifications
     */
    public static ArrayList<User> selectUserList(SelectUserListInput selectUserListInput)
            throws ClassNotFoundException, InvalidInputException, IllegalAccessException, SQLException {
        // Connecting To Database
        Connection postgresql = PostgreBootstrap.bootstrap("jdbc:postgresql://192.168.10.68:5432/servicetesterdb", "servicetester", "123qwe");

        // Preparing Query
        String query = generateQuery("SELECT", "st_user", selectUserListInput);
        PreparedStatement database = postgresql.prepareStatement(query);
        database.setString(1, SampleNettyServerHandler.convertCharArrayToString(selectUserListInput.userName));
        database.setString(2, SampleNettyServerHandler.convertCharArrayToString(selectUserListInput.email));
        database.setInt(3, SampleNettyServerHandler.convertCharArrayToInt(selectUserListInput.deptCode));

        // Executing Query
        ResultSet targetUsers = database.executeQuery(); // Rows of Users

        // Creating User List
        ArrayList<User> targetUserList = new ArrayList<User>();
        try {
            while(targetUsers.next()) { // As long as there is another user
                User user = SampleNettyServerHandler.instantiateUser(new User()); // Instantiate User with appropriate padding

                readUserFromDatabase(targetUsers, user);

                targetUserList.add(user);
            }
        }
        finally {
            // Cleaning Up
            targetUsers.close();
            database.close();
            postgresql.close();
        }
        if(targetUserList.isEmpty()) { // If no user is a match, write an empty message
            User user = SampleNettyServerHandler.instantiateUser(new User());
            targetUserList.add(user);
        }
        return targetUserList;
    }
    /* insertUser
        Input: InsertUserInput
        Output: void
     */
    public static void insertUser(InsertUserInput insertUserInput)
            throws ClassNotFoundException, InvalidInputException, IllegalAccessException, SQLException {
        // Connecting To Database
        Connection postgresql = PostgreBootstrap.bootstrap("jdbc:postgresql://192.168.10.68:5432/servicetesterdb", "servicetester", "123qwe");

        // Preparing Query
        String query = generateQuery("INSERT", "st_user", insertUserInput);
        PreparedStatement database = postgresql.prepareStatement(query);

        try {
            User newUser = insertUserInput.newUser;
            database.setInt(1, SampleNettyServerHandler.convertCharArrayToInt(newUser.userNo));
            database.setString(2, SampleNettyServerHandler.convertCharArrayToString(newUser.userId));
            database.setString(3, SampleNettyServerHandler.convertCharArrayToString(newUser.userName));
            database.setString(4, generateRandomPassword());
            database.setInt(5, SampleNettyServerHandler.convertCharArrayToInt(newUser.grade));
            database.setInt(6, SampleNettyServerHandler.convertCharArrayToInt(newUser.position));
            database.setInt(7, SampleNettyServerHandler.convertCharArrayToInt(newUser.deptCode));
            database.setInt(8, SampleNettyServerHandler.convertCharArrayToInt(newUser.deptCode2));
            database.setString(9, SampleNettyServerHandler.convertCharArrayToString(newUser.email));
            database.setString(10, SampleNettyServerHandler.convertCharArrayToString(newUser.handphone));
            database.setString(11, SampleNettyServerHandler.convertCharArrayToString(newUser.companyTelNo));
            database.setString(12, SampleNettyServerHandler.convertCharArrayToString(newUser.isUse));
            database.setTimestamp(13, SampleNettyServerHandler.convertCharArrayToTimestamp(newUser.passwordChgDate));
            database.setString(14, SampleNettyServerHandler.convertCharArrayToString(newUser.isInitPassword));
            database.setString(15, SampleNettyServerHandler.convertCharArrayToString(newUser.passwordSalt));
            database.setInt(16, SampleNettyServerHandler.convertCharArrayToInt(newUser.passwordFailCnt));
            database.setTimestamp(17, SampleNettyServerHandler.convertCharArrayToTimestamp(newUser.passwordLockDate));
            database.setString(18, SampleNettyServerHandler.convertCharArrayToString(newUser.generator));
            database.setTimestamp(19, SampleNettyServerHandler.convertCharArrayToTimestamp(newUser.generateDate));
            database.setString(20, SampleNettyServerHandler.convertCharArrayToString(newUser.amender));
            database.setTimestamp(21, SampleNettyServerHandler.convertCharArrayToTimestamp(newUser.revisionDate));
            database.setString(22, SampleNettyServerHandler.convertCharArrayToString(newUser.userLanguage));
            database.setString(23, SampleNettyServerHandler.convertCharArrayToString(newUser.certProvider));
            database.setTimestamp(24, SampleNettyServerHandler.convertCharArrayToTimestamp(newUser.expirationDate));

            // Execute Query
            database.executeUpdate();
        }
        finally {
            // Clean Up
            database.close();
            postgresql.close();
        }
    }
    public static void updateUser(UpdateUserInput updateUserInput)
            throws ClassNotFoundException, InvalidInputException, IllegalAccessException, SQLException {
        // Connecting To Database
        Connection postgresql = PostgreBootstrap.bootstrap("jdbc:postgresql://192.168.10.68:5432/servicetesterdb", "servicetester", "123qwe");

        // Preparing Query
        String query = generateQuery("UPDATE", "st_user", updateUserInput);
        PreparedStatement database = postgresql.prepareStatement(query);

        // Executing Query
        try {
            database.executeUpdate();
        }
        finally {
            database.close();
            postgresql.close();
        }
    }
    public static void deleteUser(DeleteUserInput deleteUserInput)
            throws ClassNotFoundException, InvalidInputException, IllegalAccessException, SQLException {
        // Connecting To Database
        Connection postgresql = PostgreBootstrap.bootstrap("jdbc:postgresql://192.168.10.68:5432/servicetesterdb", "servicetester", "123qwe");

        // Preparing Query
        String query = generateQuery("DELETE", "st_user", deleteUserInput);
        PreparedStatement database = postgresql.prepareStatement(query);

        // Executing Query
        try {
            database.executeUpdate();
        }
        finally {
            database.close();
            postgresql.close();
        }
    }
    // DATABASE READER =================================================================================================
    private static User readUserFromDatabase(ResultSet database, User user) throws IllegalAccessException, SQLException {
        Field[] attributes = user.getClass().getFields();
        for(Field attribute : attributes) {
            char[] value = (char[]) attribute.get(user); // Get the value of each attribute of User
            attribute.set(
                    user, SampleNettyServerHandler.convertObjectToCharArray(
                            database.getObject(camelToUnderscore(attribute.getName())),
                            value.length)
            );
        }
        return user;
    }
    // MISCELLANEOUS ===================================================================================================
    private static String generateQuery(String queryType, String tableName, Object input)
            throws IllegalAccessException, InvalidInputException {
        String sql = "";
        if("SELECT".equalsIgnoreCase(queryType)) { // Query of View User List
            String header = "length, command";
            String footer = "checksum, endMark";

            // Header
            sql = "SELECT * FROM " + tableName + " WHERE ";

            // List Conditions
            Field[] attributes = input.getClass().getFields();
            for(Field attribute : attributes) {
                String attributeName = attribute.getName();
                if(!(header.contains(attributeName) || footer.contains(attributeName))) { // Ignore header and footer
                    sql += camelToUnderscore(attributeName) + "=? OR ";
                }
            }
            sql = sql.substring(0, sql.length() - 4); // Cut off trailing ' OR '
        }
        else if("INSERT".equalsIgnoreCase(queryType)) { // Query of Insert User
            // Header
            sql = "INSERT INTO " + tableName + " (";

            // List Columns
            User newUser = ((InsertUserInput) input).newUser;

            Field[] attributes = newUser.getClass().getFields();
            for(Field attribute : attributes) {
                String attributeName = attribute.getName();
                if("userName".equals(attributeName)) {
                    sql += camelToUnderscore(attributeName) + ", user_password, ";
                }
                else {
                    sql += camelToUnderscore(attributeName) + ", ";
                }
            }
            sql = sql.substring(0, sql.length() - 2) + ") VALUES ("; // Cuts off trailing comma

            // Append Values
            for(int i = 0; i < attributes.length + 1; i++) { // User attributes + user_password
                sql += "?, ";
            }
            sql = sql.substring(0, sql.length() - 2) + ")";
        }
        else if("UPDATE".equalsIgnoreCase(queryType)) {
            // Header
            sql = "UPDATE " + tableName + " SET ";

            // Update Columns
            User targetUser = ((UpdateUserInput) input).targetUser;

            Field[] attributes = targetUser.getClass().getFields();
            for(Field attribute : attributes) {
                char[] newValue = (char[]) attribute.get(targetUser);
                String newStringValue = SampleNettyServerHandler.convertCharArrayToString(newValue);
                int newIntValue = SampleNettyServerHandler.convertCharArrayToInt(newValue);

                if (newIntValue != 0) { // Update only if new value is provided
                    String attributeName = attribute.getName();
                    if (newIntValue == -1) { // New value is String or Timestamp
                        sql += camelToUnderscore(attributeName) + "='" + newStringValue + "', ";
                    } else { // New value is int
                        sql += camelToUnderscore(attributeName) + "=" + newIntValue + ", ";
                    }
                }
            }
            sql = sql.substring(0, sql.length() - 2) + " WHERE "; // Cutting off trailing comma

            // Append Condition
            int user_no = SampleNettyServerHandler.convertCharArrayToInt(targetUser.userNo);
            if(user_no == 0) { // If user_no is not provided
                throw new InvalidInputException("Provide the user number.");
            }
            else {
                sql += "user_no=" + user_no;
            }
        }
        else if("DELETE".equalsIgnoreCase(queryType)) {
            // Header
            sql = "DELETE FROM " + tableName + " WHERE user_no IN (";

            // List User Numbers of Target Users
            ArrayList<User> targetUserList = ((DeleteUserInput) input).targetUserList;
            if(targetUserList.isEmpty()) {
                throw new InvalidInputException("Provide at least one user number.");
            }
            else {
                Iterator<User> targetUsers = targetUserList.iterator();
                while(targetUsers.hasNext()) {
                    User targetUser = targetUsers.next();
                    sql += SampleNettyServerHandler.convertCharArrayToInt(targetUser.userNo) + ", ";
                }
            }
            sql = sql.substring(0, sql.length() - 2) + ")"; // Cuts off trailing comma
        }
        return sql;
    }
    /* camelToUnderscore
    Descriptions:
    Converts variable names in camel case to variable names with underscores.
    Assumptions:
    Input is correctly formatted in camel case
 */
    private static String camelToUnderscore(String varName) {
        for(int i = 0; i < varName.length(); i++) {
            char varChar = varName.charAt(i);
            if (Character.isUpperCase(varChar) || Character.isDigit(varChar)) {
                varName = varName.substring(0,i) + "_" + Character.toLowerCase(varChar) + varName.substring(i + 1);
                i++; // counting underscore
            }
        }
        return varName;
    }
    private static String generateRandomPassword() {
        String alphabets = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*";

        String password = "";
        Random rand = new Random();
        for(int i = 0; i < 49; i++) {
            int choice = rand.nextInt(4);
            if(choice == 0) {
                int randomAlphabet = rand.nextInt(26);
                password = password.concat(String.valueOf(alphabets.charAt(randomAlphabet)));
            }
            else if(choice == 1) {
                int randomAlphabet = rand.nextInt(26);
                password = password.concat(String.valueOf(alphabets.charAt(randomAlphabet)).toUpperCase());
            }
            else if(choice == 2) {
                int randomDigit = rand.nextInt(8);
                password = password.concat(String.valueOf(digits.charAt(randomDigit)));
            }
            else if(choice == 3) {
                int randomSpecialChar = rand.nextInt(10);
                password = password.concat(String.valueOf(digits.charAt(randomSpecialChar)));
            }
        }
        password = password.concat("=");
        return password;
    }
}
