package structs;

public class UpdateUserOutput {
    // Header
    public char[] length = new char[8];
    public char[] command = new char[2];
    // Body
    public char[] errorCode = new char[8];
    public char[] errorMessage = new char[500];
    // Footer
    public char[] checksum = new char[8];
    public char[] endMark = new char[2];
}