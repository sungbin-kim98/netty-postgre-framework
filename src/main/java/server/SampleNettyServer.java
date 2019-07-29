package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;

public class SampleNettyServer {
    // Sever Logger
    static Logger logger = Logger.getLogger(SampleNettyServer.class);

    public static void main(String[] args) throws Exception {
        // Check User Input
        if(args.length != 2) {
            logger.error("Specify [(1) IP address] and [(2) port number] of Sample Netty Sever.");
            System.exit(1);
        }

        // Get User Input
        String IPAddress = args[0];
        int portNumber = 8080; // Default
        try {
            portNumber = Integer.parseInt(args[1]);
        }
        catch(NumberFormatException error) {
            logger.error("[port number] of Sample Netty server is invalid.", error);
            System.exit(1);
        }

        // EventLoopGroup respond to channel events
        EventLoopGroup parents = new NioEventLoopGroup();
        EventLoopGroup children = new NioEventLoopGroup();

        // ServerBootstrap Setting
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(parents, children) // Distinguish parent server socket and children
                    .handler(new LoggingHandler("server", LogLevel.INFO)) // Parent server socket's handler
                    .channel(NioServerSocketChannel.class) // Channel Class
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel channel) throws Exception { // How to initialize channels
                            ChannelPipeline channelPipeline = channel.pipeline();
                            // Inserts handlers to channels
                            channelPipeline.addLast(
                                    new LoggingHandler(LogLevel.INFO), // For developers
                                    new SampleNettyServerHandler() // For input and output
                            );
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // Channel Setting: Preserve inactive channels
                    .localAddress(new InetSocketAddress(IPAddress,portNumber)); // server Address
            ChannelFuture channelFuture = server.bind().sync(); // server begins operating
            channelFuture.channel().closeFuture().sync(); // Waiting until server shuts down
        }
        finally {
            // Terminate all the threads called to action
            parents.shutdownGracefully();
            children.shutdownGracefully();
        }
    }
}
