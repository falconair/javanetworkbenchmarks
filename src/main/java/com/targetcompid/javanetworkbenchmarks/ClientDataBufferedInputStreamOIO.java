package com.targetcompid.javanetworkbenchmarks;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author shahbaz
 */
public class ClientDataBufferedInputStreamOIO extends AbstractDataInputStreamClientOIO{

    public ClientDataBufferedInputStreamOIO() {
		super("OIO DataBufferedInputStream");
	}

	/**
     * Connect to server using DataInputStream with a buffer. Most obvious solution for someone who thinks about throughput.
     * @param bufferSize
     * @param TCP_NO_DELAY
     * @return
     * @throws java.io.IOException
     * @throws java.net.UnknownHostException
     */
    Map<String,String> run(int PORT, int bufferSize, boolean TCP_NO_DELAY) throws IOException, UnknownHostException {

        Socket client = new Socket(InetAddress.getLocalHost(), PORT);
        client.setTcpNoDelay(TCP_NO_DELAY);
        DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream(),bufferSize));

        try{
            startTimer();
            process(in);
            endTimer();
        }
        finally{
            client.close();
        }
        Map<String,String> res = getResults();
        res.put("TCP_NO_DELAY", Boolean.toString(TCP_NO_DELAY));
        res.put("BUFFER_SIZE", df.format(bufferSize));
        return res;
    }
}
