package com.targetcompid.javanetworkbenchmarks;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author shahbaz
 */
public class ClientDataInputStream {

    /**
     * Connect to server using DataInputStream, since server will be sending us two 'long' values. Most obvious solution.
     * @param bufferSize
     * @param TCP_NO_DELAY
     * @return
     * @throws java.io.IOException
     * @throws java.net.UnknownHostException
     */
    Map<String,String> run(int PORT, int bufferSize, boolean TCP_NO_DELAY) throws IOException, UnknownHostException {
        Parser p = new Parser("OIO DataInputStream");

        Socket client = new Socket(InetAddress.getLocalHost(), PORT);
        client.setTcpNoDelay(TCP_NO_DELAY);
        DataInputStream in = new DataInputStream(client.getInputStream());

        try{
            p.startTimer();
            p.process(in);
            p.endTimer();
        }
        finally{
            client.close();
        }
        Map<String,String> res = p.getResults();
        res.put("TCP_NO_DELAY", Boolean.toString(TCP_NO_DELAY));
        res.put("BUFFER_SIZE", Parser.df.format(0));
        return res;
    }
}
