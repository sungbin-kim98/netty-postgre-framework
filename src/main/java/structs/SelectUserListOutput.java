package structs;

import java.util.ArrayList;

public class SelectUserListOutput {
    // Header
    public char[] length = new char[8];
    public char[] command = new char[2];
    // Body
    public ArrayList<User> targetUserList;
    public char[] errorCode = new char[8];
    public char[] errorMessage = new char[500];
    // Footer
    public char[] checksum = new char[8];
    public char[] endMark = new char[2];
}