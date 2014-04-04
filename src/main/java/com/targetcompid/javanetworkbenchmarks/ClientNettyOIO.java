package com.targetcompid.javanetworkbenchmarks;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.oio.OioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * @author shahbaz
 */
public class ClientNettyOIO {

    /**
     * Connect to server using Netty's non-NIO client.
     * @param bufferSize
     * @param TCP_NO_DELAY
     * @return
     * @throws java.io.IOException
     * @throws java.net.UnknownHostException
     */
    Map<String,String> run(int PORT, int bufferSize, boolean TCP_NO_DELAY) throws IOException, UnknownHostException {
        final Parser p = new Parser("Netty OIO");

        ChannelFactory factory = new OioClientSocketChannelFactory(Executors.newCachedThreadPool());

        ClientBootstrap bootstrap = new ClientBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(
                        //Pass through frame decoder
                        new FrameDecoder() {

                            @Override
                            protected Object decode(ChannelHandlerContext ctx, Channel channel,ChannelBuffer buf) throws Exception {
                                int readableBytes = buf.readableBytes();
                                if(readableBytes < 16) return null;

                                int records = readableBytes/16;
                                return buf.readBytes(records*16);
                            }
                        },
                        //Pass received data to processor
                        new SimpleChannelHandler(){
                            @Override
                            public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
                                ChannelBuffer buf = (ChannelBuffer) e.getMessage();
                                p.process(buf);
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
                                e.getCause().printStackTrace();
                                e.getChannel().close();
                                p.endTimer();
                            }
                        });
            }
        });

        bootstrap.setOption("tcpNoDelay", TCP_NO_DELAY);

        p.startTimer();
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(InetAddress.getLocalHost(), PORT));
        future.awaitUninterruptibly();
        if (!future.isSuccess()) {
            future.getCause().printStackTrace();
        }
        future.getChannel().getCloseFuture().awaitUninterruptibly();
        factory.releaseExternalResources();
        p.endTimer();


        Map<String,String> res = p.getResults();
        res.put("TCP_NO_DELAY", Boolean.toString(TCP_NO_DELAY));
        res.put("BUFFER_SIZE", Parser.df.format(0));
        return res;
    }
}
