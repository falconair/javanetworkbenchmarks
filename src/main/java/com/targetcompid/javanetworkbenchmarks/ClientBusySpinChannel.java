package com.targetcompid.javanetworkbenchmarks;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

/**
 * @author shahbaz
 */
public class ClientBusySpinChannel {

    /**
     * Connect to server using NIO sockets, but instead of blocking, simply spin in a loop. Polling on steroids, which means
     * it probably isn't a good idea, unless you REALLY want to win.
     * @param bufferSize
     * @param TCP_NO_DELAY
     * @return
     * @throws java.io.IOException
     * @throws java.net.UnknownHostException
     */
    Map<String,String> run(int PORT, int bufferSize, boolean TCP_NO_DELAY) throws IOException, UnknownHostException {
        Parser p = new Parser("NIO Non-Blocked Spinning Channel");

        SocketChannel channel = SocketChannel.open();
        channel.setOption(StandardSocketOptions.TCP_NODELAY,TCP_NO_DELAY);
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress(InetAddress.getLocalHost(), PORT));
        channel.finishConnect();

        ByteBuffer data = ByteBuffer.allocate(bufferSize);
        int size = 0;

        try{
            p.startTimer();
            while(-1 != (size = channel.read(data))){
                if(size != 0){
                    data.flip();
                    p.process(size, data);
                    data.clear();
                }
            }
            p.endTimer();
            //System.out.println(p);
        }
        finally{
            channel.close();
        }
        Map<String,String> res = p.getResults();
        res.put("TCP_NO_DELAY", Boolean.toString(TCP_NO_DELAY));
        res.put("BUFFER_SIZE", Parser.df.format(bufferSize));
        return res;
    }
}
