package com.targetcompid.javanetworkbenchmarks;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;

/**
 * @author shahbaz
 */
public class ClientNonBlockedSelectorChannel {

    /**
     * Connect to server using NIO selectors. Most people wouldn't do this for client connections. Selectors are supposed to
     * be used by highly scalable servers.
     * @param bufferSize
     * @param TCP_NO_DELAY
     * @return
     * @throws java.io.IOException
     * @throws java.net.UnknownHostException
     */
    Map<String,String> run(int PORT, int bufferSize, boolean TCP_NO_DELAY) throws IOException, UnknownHostException {
        final Parser p = new Parser("NIO Non-Blocked Selector Channel");

        Selector selector = Selector.open();

        SocketChannel channel = SocketChannel.open();
        channel.setOption(StandardSocketOptions.TCP_NODELAY,TCP_NO_DELAY);
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        channel.connect(new InetSocketAddress(InetAddress.getLocalHost(), PORT));

        ByteBuffer data = ByteBuffer.allocate(bufferSize);
        int size = 0;
        channel.finishConnect();
        p.startTimer();

        try{
            while(true){

                selector.select();

                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while(iter.hasNext()){
                    SelectionKey key = iter.next();
                    iter.remove();
                    //remove selectionKey, since we are going to deal with it now
                    if(key.isReadable()){
                        size = ((SocketChannel)key.channel()).read(data);
                        if(size != -1){
                            data.flip();
                            p.process(size, data);
                            data.clear();
                        }
                        else{
                            p.endTimer();
                            Map<String,String> res = p.getResults();
                            res.put("TCP_NO_DELAY", Boolean.toString(TCP_NO_DELAY));
                            res.put("BUFFER_SIZE", Parser.df.format(bufferSize));
                            return res;
                        }
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            channel.close();
        }

        //never called
        Map<String,String> res = p.getResults();
        res.put("TCP_NO_DELAY", Boolean.toString(TCP_NO_DELAY));
        res.put("BUFFER_SIZE", Parser.df.format(bufferSize));
        return res;
    }
}
