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
public class ClientBlockedChannelNIO extends AbstractNIOClient{

    public ClientBlockedChannelNIO() {
		super("NIO Blocked Channel");
	}

	/**
     * Connect to server using NIO channels. However, someone using NIO isn't likely to fall back to blocking sockets.
     * @param bufferSize
     * @param TCP_NO_DELAY
     * @return
     * @throws java.io.IOException
     * @throws java.net.UnknownHostException
     */
    Map<String,String> run(int PORT, int bufferSize, boolean TCP_NO_DELAY) throws IOException, UnknownHostException {

        SocketChannel channel = SocketChannel.open();
        channel.setOption(StandardSocketOptions.TCP_NODELAY,TCP_NO_DELAY);
        channel.connect(new InetSocketAddress(InetAddress.getLocalHost(), PORT));

        ByteBuffer data = ByteBuffer.allocate(bufferSize);
        int size = 0;

        try{
            startTimer();
            while(-1 != (size = channel.read(data))){
                data.flip();
                process(size, data);
                data.clear();
            }
            endTimer();
            //System.out.println(p);
        }
        finally{
            channel.close();
        }
        Map<String,String> res = getResults();
        res.put("TCP_NO_DELAY", Boolean.toString(TCP_NO_DELAY));
        res.put("BUFFER_SIZE", df.format(bufferSize));
        return res;
    }
}
