package client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SampleNettyClientHandler extends ChannelInboundHandlerAdapter {
    final int LENGTH_BYTES = 8;
    final int COMMAND_BYTES = 2;
    final int CHECKSUM_BYTES = 8;
    final int ENDMARK_BYTES = 2;
    final String CHARSET = "UTF-8";
    // Netty Input & Output
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[Client] Channel " + ctx.channel().id() + " has been activated.");

        ByteBuf buffer = testUpdateUser();

        ctx.writeAndFlush(buffer);
    }
    public void channelRead(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Client: Channel " + ctx.channel().id() + " has received the message: " + msg);
    }
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client: Channel " + ctx.channel().id() + " has finished reading a message");
    }
    // Query Based ByteConversionTest Cases ==========================================================================================
    public ByteBuf testSelectUserList() throws Exception {
        ByteBuf buffer = Unpooled.directBuffer();
        // Writing Header
        writeIntIntoBuffer(buffer, 220, 8, CHARSET); // length
        writeStringIntoBuffer(buffer, "VL", 2, CHARSET); // command
        // Writing Body
        writeStringIntoBuffer(buffer, "Sungbin Kim", 100, CHARSET); // user name
        writeStringIntoBuffer(buffer, "abc@gmail.com", 100, CHARSET); // email
        writeIntIntoBuffer(buffer, 11000501, 8, CHARSET); // department code
        // Writing Footer
        writeIntIntoBuffer(buffer, 34245435, 8, CHARSET); // checksum
        writeStringIntoBuffer(buffer, "@@", 2, CHARSET); // endmark
        return buffer;
    }
    public ByteBuf testSelectUser() throws Exception {
        ByteBuf buffer = Unpooled.directBuffer();
        // Writing Header
        writeIntIntoBuffer(buffer, 112, 8, CHARSET); // length
        writeStringIntoBuffer(buffer, "VI", 2, CHARSET); // command
        // Writing Body
        writeIntIntoBuffer(buffer, 4, 8, CHARSET); // user number
        // Writing Footer
        writeIntIntoBuffer(buffer, 342454, 8, CHARSET); // checksum
        writeStringIntoBuffer(buffer, "@@", 2, CHARSET); // endmark
        return buffer;
    }
    public ByteBuf testInsertUser() throws Exception {
        ByteBuf buffer = Unpooled.directBuffer();
        // Writing Header
        writeIntIntoBuffer(buffer,955, 8, CHARSET); // length
        writeStringIntoBuffer(buffer, "IN", 2, CHARSET); // command
        // Writing Body
        writeIntIntoBuffer(buffer, 5, 8, CHARSET); // user number
        writeStringIntoBuffer(buffer, "Sungbin Kim", 100, CHARSET); // user id
        writeStringIntoBuffer(buffer, "bin6704", 100, CHARSET); // user name
        writeIntIntoBuffer(buffer, 2, 2, CHARSET); // user grade
        writeIntIntoBuffer(buffer, 1, 2, CHARSET); // user position
        writeIntIntoBuffer(buffer, 100000, 8, CHARSET); // department code 1
        writeIntIntoBuffer(buffer, 0, 8, CHARSET); // department code 2
        writeStringIntoBuffer(buffer, "bin6704@gmail.com", 100, CHARSET); // email
        writeStringIntoBuffer(buffer, "010-6716-3746", 15, CHARSET); // handphone
        writeStringIntoBuffer(buffer, "822-300-9026", 15, CHARSET); // company tel no
        writeStringIntoBuffer(buffer, "1", 1, CHARSET); // isUse
        writeStringIntoBuffer(buffer, "2019-07-18 15:26:38", 35, CHARSET); // password chg date
        writeStringIntoBuffer(buffer, "1", 1, CHARSET); // isInitPassword
        writeStringIntoBuffer(buffer, "Xls=", 4, CHARSET); // password salt
        writeIntIntoBuffer(buffer, 0, 4, CHARSET); // password fail cnt
        writeStringIntoBuffer(buffer, null, 35, CHARSET); // password lock date
        writeStringIntoBuffer(buffer, "Sungbin Kim", 100, CHARSET); // generator
        writeStringIntoBuffer(buffer, "2019-07-18 15:30:38", 35, CHARSET); // generate date
        writeStringIntoBuffer(buffer,null, 100, CHARSET); // amender
        writeStringIntoBuffer(buffer, null, 35, CHARSET); // revision date
        writeStringIntoBuffer(buffer, "ko-KR", 100, CHARSET); // user language
        writeStringIntoBuffer(buffer, "Inside", 100, CHARSET); // cert provider
        writeStringIntoBuffer(buffer, null, 35, CHARSET); // expiration date
        // Writing Footer
        writeIntIntoBuffer(buffer, 342454, 8, CHARSET); // checksum
        writeStringIntoBuffer(buffer, "@@", 2, CHARSET); // footer
        return buffer;
    }
    private ByteBuf testUpdateUser() throws Exception {
        ByteBuf buffer = Unpooled.directBuffer();
        // Writing Header
        writeIntIntoBuffer(buffer, 955, 8, CHARSET);
        writeStringIntoBuffer(buffer, "UP", 2, CHARSET);
        // Writing Body
        writeIntIntoBuffer(buffer, 5, 8, CHARSET); // user number
        writeStringIntoBuffer(buffer, "bin6704", 100, CHARSET); // user id
        writeStringIntoBuffer(buffer, "Sungbin Kim", 100, CHARSET); // user name
        writeIntIntoBuffer(buffer, 2, 2, CHARSET); // user grade
        writeIntIntoBuffer(buffer, 1, 2, CHARSET); // user position
        writeIntIntoBuffer(buffer, 100000, 8, CHARSET); // department code 1
        writeIntIntoBuffer(buffer, 0, 8, CHARSET); // department code 2
        writeStringIntoBuffer(buffer, "bin6704@gmail.com", 100, CHARSET); // email
        writeStringIntoBuffer(buffer, "010-6716-3746", 15, CHARSET); // handphone
        writeStringIntoBuffer(buffer, "822-300-9026", 15, CHARSET); // company tel no
        writeStringIntoBuffer(buffer, "1", 1, CHARSET); // isUse
        writeStringIntoBuffer(buffer, "2019-07-18 15:26:38", 35, CHARSET); // password chg date
        writeStringIntoBuffer(buffer, "1", 1, CHARSET); // isInitPassword
        writeStringIntoBuffer(buffer, "Xls=", 4, CHARSET); // password salt
        writeIntIntoBuffer(buffer, 0, 4, CHARSET); // password fail cnt
        writeStringIntoBuffer(buffer, null, 35, CHARSET); // password lock date
        writeStringIntoBuffer(buffer, "Sungbin Kim", 100, CHARSET); // generator
        writeStringIntoBuffer(buffer, "2019-07-18 15:30:38", 35, CHARSET); // generate date
        writeStringIntoBuffer(buffer,null, 100, CHARSET); // amender
        writeStringIntoBuffer(buffer, null, 35, CHARSET); // revision date
        writeStringIntoBuffer(buffer, "ko-KR", 100, CHARSET); // user language
        writeStringIntoBuffer(buffer, "Inside", 100, CHARSET); // cert provider
        writeStringIntoBuffer(buffer, null, 35, CHARSET); // expiration date
        // Writing Footer
        writeIntIntoBuffer(buffer, 342454, 8, CHARSET); // checksum
        writeStringIntoBuffer(buffer, "@@", 2, CHARSET); // footer
        return buffer;
    }
    private ByteBuf testDeleteUser() throws Exception {
        ByteBuf buffer = Unpooled.directBuffer();
        // Writing Header
        writeIntIntoBuffer(buffer,20, 8, CHARSET); // length
        writeStringIntoBuffer(buffer, "DE", 2, CHARSET); // command
        // Writing Body
        writeIntIntoBuffer(buffer,5, 8, CHARSET); // user number
        // Writing Footer
        writeIntIntoBuffer(buffer, 342454, 8, CHARSET); // checksum
        writeStringIntoBuffer(buffer, "@@", 2, CHARSET); // endmark
        return buffer;
    }
    // Buffer Readers & Writers ========================================================================================
    public void writeStringIntoBuffer(ByteBuf buffer, String s, int numChars, String UTF) throws Exception {
        if(s == null) {
            s = "";
        }
        String str = String.format("%" + numChars + "s", s);
        byte[] bytes = str.getBytes(UTF);
        buffer.writeBytes(bytes);
    }
    public void writeIntIntoBuffer(ByteBuf buffer, int z, int numChars, String UTF) throws Exception {
        String str = String.format("%0" + numChars + "d", z);
        byte[] bytes = str.getBytes(UTF);
        buffer.writeBytes(bytes);
    }
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
