package structs;

import java.util.ArrayList;

public class DeleteUserInput {
    // Header
    public char[] length = new char[8];
    public char[] command = new char[2];
    // Body
    public ArrayList<User> targetUserList;
    // Footer
    public char[] checksum = new char[8];
    public char[] endMark = new char[2];
}