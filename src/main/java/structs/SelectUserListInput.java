package structs;

public class SelectUserListInput {
    // Header
    public char[] length = new char[8];
    public char[] command = new char[2];
    // Body
    public char[] userName = new char[100];
    public char[] email = new char[100];
    public char[] deptCode = new char[8];
    // Footer
    public char[] checksum = new char[8];
    public char[] endMark = new char[2];
}
