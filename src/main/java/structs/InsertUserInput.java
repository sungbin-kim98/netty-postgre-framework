package structs;

public class InsertUserInput {
    // Header
    public char[] length = new char[8];
    public char[] command = new char[2];
    // Body
    public User newUser;
    // Footer
    public char[] checksum = new char[8];
    public char[] endMark = new char[2];
}