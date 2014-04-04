package com.targetcompid.javanetworkbenchmarks;


import static java.lang.System.out;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/***
 *
 * @author Shahbaz Chaudhary
 *
 */
public class Server {

    public long run(int PORT) throws IOException {
        final ServerSocket server = new ServerSocket(PORT);
        out.println("TCP socket server started");

        //Accept single connection for this test
        final Socket c1 = server.accept();
        c1.setTcpNoDelay(true);
        out.println("Client connected");

        long counter = 0;
        DataOutputStream serverout;

        serverout = new DataOutputStream(c1.getOutputStream());

        try{
            //Keep sending data until client ends connection
            for (int i = 0; i < Long.MAX_VALUE; i++) {
                serverout.writeLong(counter);
                serverout.writeLong(System.nanoTime());
                counter++;
            }
        }
        catch(SocketException e){
            //ignore exception, just means client disconnected
        }
        finally{
            c1.close();
        }

        //Return counter to avoid optimizers from being too aggressive
        return counter;
    }

    public static void main(String ... args) throws IOException {

        if(args.length < 1){
            out.println("Port missing. Please provide a numeric port number");
            System.exit(-1);
        }

        int port = Integer.parseInt(args[0]);

        Server server = new Server();
        long iterations = server.run(port);

        out.println(String.format("Iterations: %s, bytes: %s",iterations,iterations * 64 * 2));
    }
}
