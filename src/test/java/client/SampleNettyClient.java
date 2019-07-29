package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.Logger;

public class SampleNettyClient {
    // Sever Logger
    static Logger logger = Logger.getLogger(SampleNettyClient.class);

    public static void main(String[] args) throws Exception {
        // Check User Input
        if(args.length != 2) {
            logger.error("Specify [(1) IP Address] and [(2) Port Number] of Sample Netty Server to connect to");
            System.exit(1);
        }

        // Get User Input
        String serverAddress = args[0];
        int portNumber = 8080; // Default
        try {
            portNumber = Integer.parseInt(args[1]);
        }
        catch(NumberFormatException error) {
            logger.error("[port number] of Sample Netty Server is invalid.", error);
            System.exit(1);
        }

        EventLoopGroup thread = new NioEventLoopGroup();
        try {
            Bootstrap client = new Bootstrap();
            client.group(thread)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.AUTO_READ, false)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline channelPipeline = channel.pipeline();
                            channelPipeline.addLast(
                                    new LoggingHandler(LogLevel.INFO),
                                    new SampleNettyClientHandler()
                            );
                        }
                    });
            ChannelFuture channelFuture = client.connect(serverAddress, portNumber).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            thread.shutdownGracefully();
        }
    }
}
