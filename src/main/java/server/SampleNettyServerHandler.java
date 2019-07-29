package server;

import errors.InvalidInputException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;
import structs.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

public class SampleNettyServerHandler extends ChannelInboundHandlerAdapter {
    // SERVER SETTING ==================================================================================================
    // Header Struct
    final int LENGTH_BYTES = 8;
    final int COMMAND_BYTES = 2;
    // Footer Struct
    final int CHECKSUM_BYTES = 8;
    final int ENDMARK_BYTES = 2;
    // Charset Choice
    final String CHARSET = "UTF-8";
    /* Standard Charsets
        Source: https://docs.oracle.com/javase/9/docs/api/java/nio/charset/Charset.html
        US-ASCII
        ISO-8859-1
        CHARSET-8
        CHARSET-16BE
        CHARSET-16LE
        CHARSET-16
     */
    // Sever Logger
    static Logger logger = Logger.getLogger(SampleNettyServerHandler.class);
    // CHANNEL EVENTS ==================================================================================================
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // Preparing Input and Output Buffer
        ByteBuf inputBuffer = (ByteBuf) msg;
        ByteBuf outputBuffer = Unpooled.directBuffer();

        // Read Buffer Length and Command
        int inputBufferLength = readIntFromBuffer(inputBuffer, LENGTH_BYTES, CHARSET);
        String command = readStringFromBuffer(inputBuffer, COMMAND_BYTES, CHARSET);

        // Executing Command & Preparing Output
        if("VI".equals(command)) { // View User
            SelectUserOutput selectUserOutput = new SelectUserOutput();
            selectUserOutput.command = convertStringToCharArray("VI", COMMAND_BYTES);
            try {
                // Reading Input
                SelectUserInput targetUser = readSelectUserInputFromBuffer(inputBuffer, CHARSET);

                // Performing Database Query
                User user = SampleNettyServerPostgre.selectUser(targetUser);
                selectUserOutput.user = user;

                // If Successful Till Here, Write Empty Error Report
                selectUserOutput.errorCode = convertNumberToCharArray(0, selectUserOutput.errorCode.length);
                selectUserOutput.errorMessage = convertStringToCharArray("", selectUserOutput.errorMessage.length);

                // Writing Output
                writeStructIntoBuffer(outputBuffer, selectUserOutput, CHARSET);
            }
            catch(Exception error) {
                // To Avoid Null Pointer Error, Instantiate Empty User
                selectUserOutput.user = instantiateUser(new User());

                // Write Error Report
                writeErrorIntoOutput(selectUserOutput, error);

                // Writing Output
                writeStructIntoBuffer(outputBuffer, selectUserOutput, CHARSET);
            }
        }
        else if("VL".equals(command)) { // View User List
            SelectUserListOutput selectUserListOutput = new SelectUserListOutput();
            selectUserListOutput.command = convertStringToCharArray("VL", COMMAND_BYTES);
            try {
                // Reading Input
                SelectUserListInput targetUser = readSelectUserListInputFromBuffer(inputBuffer, CHARSET);

                // Performing Database Query
                ArrayList<User> targetUserList = SampleNettyServerPostgre.selectUserList(targetUser);
                selectUserListOutput.targetUserList = targetUserList;

                // If Successful Till Here, Write Empty Error Report
                selectUserListOutput.errorCode = convertNumberToCharArray(0, selectUserListOutput.errorCode.length);
                selectUserListOutput.errorMessage = convertStringToCharArray("", selectUserListOutput.errorMessage.length);

                // Writing Output
                writeStructIntoBuffer(outputBuffer, selectUserListOutput, CHARSET);
            }
            catch(Exception error) {
                // To Avoid Null Pointer Error, Instantiate Empty User List
                selectUserListOutput.targetUserList = new ArrayList<User>();

                // Write Error Report
                writeErrorIntoOutput(selectUserListOutput, error);

                // Writing Output
                writeStructIntoBuffer(outputBuffer, selectUserListOutput, CHARSET);
            }
        }
        else if("IN".equals(command)) { // Insert User
            InsertUserOutput insertUserOutput = new InsertUserOutput();
            insertUserOutput.command = convertStringToCharArray("IN", COMMAND_BYTES);
            try {
                // Reading Input
                InsertUserInput newUser = readInsertUserInputFromBuffer(inputBuffer, CHARSET);

                // Performing Database Query
                SampleNettyServerPostgre.insertUser(newUser);

                // If Successful Till Here, Write Empty Error Report
                insertUserOutput.errorCode = convertNumberToCharArray(0, insertUserOutput.errorCode.length);
                insertUserOutput.errorMessage = convertStringToCharArray("", insertUserOutput.errorMessage.length);

                // Writing Output
                writeStructIntoBuffer(outputBuffer, insertUserOutput, CHARSET);
            }
            catch(Exception error) {
                // Write Error Report
                writeErrorIntoOutput(insertUserOutput, error);

                // Writing Output
                writeStructIntoBuffer(outputBuffer, insertUserOutput, CHARSET);
            }
        }
        else if("UP".equals(command)) { // Update User
            UpdateUserOutput updateUserOutput = new UpdateUserOutput();
            updateUserOutput.command = convertStringToCharArray("UP", COMMAND_BYTES);
            try {
                // Reading Input
                UpdateUserInput targetUser = readUpdateUserInputFromBuffer(inputBuffer, CHARSET);

                // Performing Database Query
                SampleNettyServerPostgre.updateUser(targetUser);

                // If Successful Till Here, Write Empty Error Report
                updateUserOutput.errorCode = convertNumberToCharArray(0, updateUserOutput.errorCode.length);
                updateUserOutput.errorMessage = convertStringToCharArray("", updateUserOutput.errorMessage.length);

                // Writing Output
                writeStructIntoBuffer(outputBuffer, updateUserOutput, CHARSET);
            }
            catch(Exception error) {
                // Write Error Report
                writeErrorIntoOutput(updateUserOutput, error);

                // Writing Output
                writeStructIntoBuffer(outputBuffer, updateUserOutput, CHARSET);
            }
        }
        else if("DE".equals(command)) { // Delete User
            DeleteUserOutput deleteUserOutput = new DeleteUserOutput();
            deleteUserOutput.command = convertStringToCharArray("DE", COMMAND_BYTES);
            try {
                // Reading Input
                DeleteUserInput targetUser = readDeleteUserInputFromBuffer(inputBuffer, CHARSET);

                // Performing Database Query
                SampleNettyServerPostgre.deleteUser(targetUser);

                // If Successful Till Here, Write Empty Error Report
                deleteUserOutput.errorCode = convertNumberToCharArray(0, deleteUserOutput.errorCode.length);
                deleteUserOutput.errorMessage = convertStringToCharArray("", deleteUserOutput.errorMessage.length);

                // Writing Output
                writeStructIntoBuffer(outputBuffer, deleteUserOutput, CHARSET);
            }
            catch(Exception error) {
                // Write Error Report
                writeErrorIntoOutput(deleteUserOutput, error);

                // Writing Output
                writeStructIntoBuffer(outputBuffer, deleteUserOutput, CHARSET);
            }
        }
        if(outputBuffer.writerIndex() == 0) { // If command was wrong and output is empty
            throw new InvalidInputException("Command must be one of ['VI','VL','IN','UP','DE']");
        }
        // Sending Message
        ctx.writeAndFlush(outputBuffer);
    }
    public void channelReadComplete(ChannelHandlerContext ctx) {
    }
    // Buffer Readers ==================================================================================================
    private SelectUserInput readSelectUserInputFromBuffer(ByteBuf buffer, String CHARSET)
            throws IllegalAccessException, InvalidInputException, UnsupportedEncodingException {
        buffer.resetReaderIndex(); // Read from the beginning

        SelectUserInput selectUserInput = new SelectUserInput();

        readStructFromBuffer(buffer, selectUserInput, CHARSET);

        return selectUserInput;
    }
    private SelectUserListInput readSelectUserListInputFromBuffer(ByteBuf buffer, String CHARSET)
            throws IllegalAccessException, InvalidInputException, UnsupportedEncodingException {
        buffer.resetReaderIndex(); // Read from the beginning

        SelectUserListInput selectUserListInput = new SelectUserListInput();

        readStructFromBuffer(buffer, selectUserListInput, CHARSET);

        return selectUserListInput;
    }
    private InsertUserInput readInsertUserInputFromBuffer(ByteBuf buffer, String CHARSET)
            throws IllegalAccessException, InvalidInputException, UnsupportedEncodingException {
        buffer.resetReaderIndex(); // Read from the beginning

        InsertUserInput insertUserInput = new InsertUserInput();
        insertUserInput.newUser = instantiateUser(new User()); // Avoid Null Pointer

        readStructFromBuffer(buffer, insertUserInput, CHARSET);

        return insertUserInput;
    }
    private UpdateUserInput readUpdateUserInputFromBuffer(ByteBuf buffer, String CHARSET)
            throws IllegalAccessException, InvalidInputException, UnsupportedEncodingException {
        buffer.resetReaderIndex(); // Read from the beginning

        UpdateUserInput updateUserInput = new UpdateUserInput();
        updateUserInput.targetUser = instantiateUser(new User()); // Avoid Null Pointer

        readStructFromBuffer(buffer, updateUserInput, CHARSET);

        return updateUserInput;
    }
    private DeleteUserInput readDeleteUserInputFromBuffer(ByteBuf buffer, String CHARSET)
            throws IllegalAccessException, InvalidInputException, UnsupportedEncodingException {
        buffer.resetReaderIndex(); // Read from the beginning

        DeleteUserInput deleteUserInput = new DeleteUserInput();
        deleteUserInput.targetUserList = new ArrayList<User>();

        readStructFromBuffer(buffer, deleteUserInput, CHARSET);

        return deleteUserInput;
    }
    private Object readStructFromBuffer(ByteBuf buffer, Object input, String CHARSET)
            throws IllegalAccessException, InvalidInputException, UnsupportedEncodingException {
        int bufferLength = 0;

        Field[] attributes = input.getClass().getFields();
        for(Field attribute : attributes) {
            Object value = attribute.get(input); // Get each attribute
            if(value instanceof Iterable) { // For Delete User
                int numUsers = (bufferLength - COMMAND_BYTES - CHECKSUM_BYTES - ENDMARK_BYTES) / 8;

                // numUsers must be a nonnegative integer
                try {
                    assert(numUsers >= 0);
                }
                catch(AssertionError error) {
                    throw new InvalidInputException("Invalid input buffer size.");
                }

                for(int i = 0; i < numUsers; i++) {
                    User user = instantiateUser(new User());
                    user.userNo = readCharArrayFromBuffer(buffer, 8, CHARSET); // Read User Number
                    ((ArrayList<User>) value).add(user);
                }
            }
            else if(value instanceof User) {
                readUserFromBuffer(buffer, (User) value, CHARSET);
            }
            else {
                String attributeName = attribute.getName();
                if("length".equals(attributeName)) {
                    bufferLength = readIntFromBuffer(buffer, LENGTH_BYTES, CHARSET); // Store Buffer Length For Later
                    attribute.set(input, convertNumberToCharArray(bufferLength, LENGTH_BYTES));
                }
                else if("endMark".equals(attributeName)) {
                    String endMark = readStringFromBuffer(buffer, ENDMARK_BYTES, CHARSET);
                    try {
                        assert("@@".equals(endMark));
                    }
                    catch(AssertionError error) {
                        throw new InvalidInputException("Input end mark has not been found.");
                    }
                    attribute.set(input, convertStringToCharArray(endMark, ENDMARK_BYTES));
                }
                else {
                    attribute.set(input, readCharArrayFromBuffer(buffer, ((char[]) value).length, CHARSET)); // Update
                }
            }
        }
        return input;
    }
    private User readUserFromBuffer(ByteBuf buffer, User user, String CHARSET)
            throws IllegalAccessException, UnsupportedEncodingException {
        Field[] attributes = user.getClass().getFields();
        for(Field attribute : attributes) {
            char[] value = (char[]) attribute.get(user);
            attribute.set(user, readCharArrayFromBuffer(buffer, value.length, CHARSET));
        }
        return user;
    }
    /* readCharArrayFromBuffer
        Descriptions:
        Think of this as the converter from byte[] to char[]
     */
    public static char[] readCharArrayFromBuffer(ByteBuf buffer, int numChars, String CHARSET) throws UnsupportedEncodingException {
        byte[] bytes = new String(new char[numChars]).getBytes(CHARSET); // Translate numChars to numBytes
        buffer.readBytes(bytes); // Reads as many bytes as numBytes
        char[] chars = new String(bytes, CHARSET).toCharArray(); // byte[] to char[]
        return chars;
    }
    /* readStringFromBuffer
        Descriptions:
        Think of this as the converter from byte[] to String
     */
    public static String readStringFromBuffer(ByteBuf buffer, int numChars, String CHARSET) throws UnsupportedEncodingException {
        byte[] bytes = new String(new char[numChars]).getBytes(CHARSET);
        buffer.readBytes(bytes);
        String str = new String(bytes, CHARSET).trim();
        return str;
    }
    /* readIntFromBuffer
        Descriptions:
        Think of this as the converter from byte[] to int
     */
    public static int readIntFromBuffer(ByteBuf buffer, int numChars, String CHARSET) throws UnsupportedEncodingException {
        String str = readStringFromBuffer(buffer, numChars, CHARSET);
        int num = Integer.parseInt(str);
        return num;
    }
    // Buffer Writers ==================================================================================================
    private void writeStructIntoBuffer(ByteBuf buffer, Object output, String CHARSET)
            throws IllegalAccessException, UnsupportedEncodingException {
        if(output instanceof User) { // Writing User
            Field[] attributes = output.getClass().getFields();
            for(Field attribute : attributes) {
                char[] value = (char[]) attribute.get(output); // Get the value of each attribute
                writeCharArrayIntoBuffer(buffer, value, CHARSET);
            }
        }
        else { // Writing Output
            // Writing Body
            Field[] attributes = output.getClass().getFields();
            for(Field attribute : attributes) {
                Object value = attribute.get(output); // Get the value of each attribute
                if(value instanceof Iterable) { // If UserList
                    Iterator iterator = ((Iterable) value).iterator();
                    while(iterator.hasNext()) {
                        writeStructIntoBuffer(buffer, (User) iterator.next(), CHARSET); // Write User
                    }
                }
                else if(value instanceof User) { // If User
                    writeStructIntoBuffer(buffer, (User) value, CHARSET); // Write User
                }
                else { // If char[] attributes
                    writeCharArrayIntoBuffer(buffer, (char[]) value, CHARSET);
                }
            }

            // Writing Header
            int length = buffer.writerIndex() - LENGTH_BYTES; // length = buffer size - length size
            buffer.markWriterIndex(); // Recording buffer size
            buffer.writerIndex(0); // Write length at beginning
            writeNumberIntoBuffer(buffer, length, LENGTH_BYTES, CHARSET);

            // Writing Footer
            buffer.resetWriterIndex(); // Remember buffer size
            buffer.writerIndex(buffer.writerIndex() - CHECKSUM_BYTES - ENDMARK_BYTES); // Go to checksum's position
            long checksum = calculateChecksum(buffer);
            writeNumberIntoBuffer(buffer, checksum, CHECKSUM_BYTES, CHARSET);

            // Sealing Output
            writeStringIntoBuffer(buffer, "@@", ENDMARK_BYTES, CHARSET);

            // Preparing For Delivery
            buffer.resetReaderIndex();
        }
    }
    /* writeCharArrayIntoBuffer

     */
    public static void writeCharArrayIntoBuffer(ByteBuf buffer, char[] chars, String CHARSET) throws UnsupportedEncodingException {
        String str = String.valueOf(chars);
        byte[] bytes = str.getBytes(CHARSET);
        buffer.writeBytes(bytes);
    }
    public static void writeStringIntoBuffer(ByteBuf buffer, String str, int numChars, String CHARSET) throws UnsupportedEncodingException {
        String formattedStr = String.format("%-" + numChars + "s", str);
        byte[] bytes = formattedStr.getBytes(CHARSET);
        buffer.writeBytes(bytes);
    }
    public static void writeNumberIntoBuffer(ByteBuf buffer, Number num, int numChars, String CHARSET) throws UnsupportedEncodingException {
        String formattedStr = String.format("%0" + numChars + "d", num);
        byte[] bytes = formattedStr.getBytes(CHARSET);
        buffer.writeBytes(bytes);
    }
    // BUFFER WRITER ACCESSORIES =======================================================================================
    /* calculateChecksum
        Descriptions:
            Encodes buffer's body into checksum
        Input:
            buffer whose writerIndex is positioned at the end of message.
        Output:
            checksum
     */
    private long calculateChecksum(ByteBuf buffer) {
        // Read Body
        buffer.readerIndex(LENGTH_BYTES + COMMAND_BYTES); // Skip header
        byte[] bytes = new byte[buffer.writerIndex() - CHECKSUM_BYTES - ENDMARK_BYTES]; // Skip footer
        buffer.readBytes(bytes);

        // Convert To Long
        long longBytes = 0;
        for(int i = 0; i < bytes.length; i++) {
            longBytes <<= 8;
            longBytes |= (bytes[i] & 0xFF);
        }

        // Represent as decimal
        String decimalLongBytes = Long.toString(longBytes);
        int decimalLongBytesLength = decimalLongBytes.length();
        if(decimalLongBytesLength > 8) {
            decimalLongBytes = decimalLongBytes.substring(decimalLongBytesLength - 8); // Want only last 8 digits
        }

        // Reset Buffer ReaderIndex
        buffer.resetReaderIndex();

        return Long.parseLong(decimalLongBytes);
    }
    // DATA TYPE CONVERTERS ============================================================================================
    /* convertStringToCharArray
        Descriptions:
        Converts String to char[] with fixed length (numBytes) padded by spaces
     */
    public static char[] convertStringToCharArray(String str, int numChars) {
        if(str == null) {
            str = "";
        }
        String formattedStr = String.format("%-" + numChars + "s", str);
        return formattedStr.toCharArray();
    }
    public static String convertCharArrayToString(char[] chars) {
        String str = String.valueOf(chars);
        str = str.trim();
        return str;
    }
    /* convertNumberToCharArray
        Descriptions:
        Converts Number to char[] with fixed length (numBytes) padded by zeros
     */
    public static char[] convertNumberToCharArray(Number num, int numChars) {
        if(num instanceof BigDecimal) {
            num = ((BigDecimal) num).toBigInteger();
        }
        String str = String.format("%0" + numChars + "d", num); // numero padding with a fixed length
        return str.toCharArray();
    }
    public static int convertCharArrayToInt(char[] chars) {
        String str = convertCharArrayToString(chars);
        // Convert String to int
        int num;
        if("".equals(str)) {
            num = 0;
        }
        else {
            try {
                num = Integer.parseInt(str);
            }
            catch(NumberFormatException error) {
                num = -1;
                return num;
            }
        }
        return num;
    }
    /* convertTimestampToCharArray
        Descriptions:
        Converts Timestamp to char[] with fixed length (numBytes) padded by spaces
     */
    public static char[] convertTimestampToCharArray(Timestamp timestamp, int numChars) {
        // Convert Timestamp to String
        String str;
        if(timestamp == null) {
            str = "";
        }
        else {
            str = timestamp.toString();
        }
        return convertStringToCharArray(str, numChars);
    }
    public static Timestamp convertCharArrayToTimestamp(char[] chars) {
        // Char[] to String
        String str = convertCharArrayToString(chars);
        // String to Timestamp
        Timestamp timestamp;
        if("".equals(str)) {
            timestamp = null;
        }
        else {
            timestamp = Timestamp.valueOf(str);
        }
        return timestamp;
    }
    /* convertObjectToCharArray
        Descriptions:
        Generalized converter that can convert any Object to char[]
     */
    public static char[] convertObjectToCharArray(Object obj, int numChars) {
        // Padding Appropriately
        String formattedStr;
        if(obj instanceof Number) {
            // Converting To Number
            return convertNumberToCharArray((Number) obj, numChars); // Padding with zeros
        }
        else {
            // Converting To String
            String str;
            if(obj == null) {
                str = "";
            }
            else {
                str = obj.toString();
            }
            return convertStringToCharArray(str, numChars); // Padding with spaces
        }
    }
    // STRUCT CONSTRUCTOR ==============================================================================================
    /* instantiateUser
        Descriptions:
            Fill user's attributes with appropriate default values. 0 for numbers and space for Strings.
     */
    public static User instantiateUser(User user) {
        user.userNo = convertNumberToCharArray(0, user.userNo.length);
        user.userId = convertStringToCharArray("", user.userId.length);
        user.userName = convertStringToCharArray("", user.userName.length);
        user.grade = convertNumberToCharArray(0, user.grade.length);
        user.position = convertNumberToCharArray(0, user.position.length);
        user.deptCode = convertNumberToCharArray(0, user.deptCode.length);
        user.deptCode2 = convertNumberToCharArray(0, user.deptCode2.length);
        user.email = convertStringToCharArray("", user.email.length);
        user.handphone = convertStringToCharArray("", user.handphone.length);
        user.companyTelNo = convertStringToCharArray("", user.companyTelNo.length);
        user.isUse = convertStringToCharArray("", user.isUse.length);
        user.passwordChgDate= convertStringToCharArray("", user.passwordChgDate.length);
        user.isInitPassword = convertStringToCharArray("", user.isInitPassword.length);
        user.passwordSalt = convertStringToCharArray("", user.passwordSalt.length);
        user.passwordFailCnt = convertNumberToCharArray(0, user.passwordFailCnt.length);
        user.passwordLockDate = convertStringToCharArray("", user.passwordLockDate.length);
        user.generator= convertStringToCharArray("", user.generator.length);
        user.generateDate = convertStringToCharArray("", user.generateDate.length);
        user.amender = convertStringToCharArray("", user.amender.length);
        user.revisionDate = convertStringToCharArray("", user.revisionDate.length);
        user.userLanguage= convertStringToCharArray("", user.userLanguage.length);
        user.certProvider = convertStringToCharArray("", user.certProvider.length);
        user.expirationDate = convertStringToCharArray("", user.expirationDate.length);
        return user;
    }
    // ERROR HANDLERS ==================================================================================================
    private void writeErrorIntoOutput(Object output, Throwable error) throws IllegalAccessException {
        // Logs Into Log File
        logger.error(error.getMessage(), error);

        // Logs Into Output
        Field[] attributes = output.getClass().getFields();
        for(Field attribute : attributes) {
            String attributeName = attribute.getName();
            if("errorCode".equals(attributeName)) {
                char[] value = (char[]) attribute.get(output);
                if(error instanceof SQLException) {
                    attribute.set(output, convertNumberToCharArray(((SQLException) error).getErrorCode(), value.length));
                }
                else if(error instanceof ClassNotFoundException) {
                    attribute.set(output, convertNumberToCharArray(4003, value.length));
                }
            }
            else if("errorMessage".equals(attributeName)) {
                char[] value = (char[]) attribute.get(output);
                attribute.set(output, convertStringToCharArray(((Exception) error).getMessage(), value.length));
            }
        }
    }
    /* Errors To Be Reckoned With
    1. Invalid Command Error (When command is neither 'VL', 'VI', 'IN', 'UP', 'DE')
    2. Input Not Provided For 'UP' (User number must be provided)
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(cause.getMessage(), cause);
        ctx.close();
    }
}
