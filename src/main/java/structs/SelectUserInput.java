package structs;

public class SelectUserInput {
    // Header
    public char[] length = new char[8];
    public char[] command = new char[2];
    // Body
    public char[] userNo = new char[8];
    // Footer
    public char[] checksum = new char[8];
    public char[] endMark = new char[2];
}
