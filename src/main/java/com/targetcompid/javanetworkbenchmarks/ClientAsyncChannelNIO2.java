package com.targetcompid.javanetworkbenchmarks;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author shahbaz
 */
public class ClientAsyncChannelNIO2 extends AbstractNIOClient{

    public ClientAsyncChannelNIO2() {
		super("NIO2 Async");
	}

	/**
     * NIO2 way of connecting to servers. Poor documentation and few blogs showing how to do it right. Combines the complexity
     * of ByteBuffers, async programming and recursive calls. CRUD/JDBC developers should think twice before using this
     * instead of basic stream.
     * @param bufferSize
     * @param TCP_NO_DELAY
     * @return
     * @throws java.io.IOException
     * @throws java.net.UnknownHostException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    Map<String,String> run(int PORT, int bufferSize, boolean TCP_NO_DELAY) throws IOException, UnknownHostException, InterruptedException, ExecutionException {
        final CountDownLatch latch = new CountDownLatch(1);

        final AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        channel.setOption(StandardSocketOptions.TCP_NODELAY,TCP_NO_DELAY);
        Future<Void> connectFuture = channel.connect(new InetSocketAddress(InetAddress.getLocalHost(), PORT));
        connectFuture.get();

        final ByteBuffer data = ByteBuffer.allocate(bufferSize);

        try{
            startTimer();
            channel.read(data, null, new CompletionHandler<Integer, Void>() {

                @Override
                public void completed(Integer result, Void att) {
                    final int size = result.intValue();

                    if(size != -1){
                        data.flip();
                        process(size, data);
                        data.clear();
                        channel.read(data,null,this);
                    }
                    else{
                        endTimer();
                        latch.countDown();
                    }
                }

                @Override
                public void failed(Throwable exc, Void att) {
                    exc.printStackTrace();
                }
            });

            latch.await();
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
