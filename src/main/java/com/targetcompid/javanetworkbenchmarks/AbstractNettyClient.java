package com.targetcompid.javanetworkbenchmarks;

import org.jboss.netty.buffer.ChannelBuffer;

public class AbstractNettyClient extends AbstractClient{
	
	public AbstractNettyClient(String id) {
		super(id);
	}

	public void process(ChannelBuffer buf) {

		while(buf.readableBytes() >= 16){
			long remoteTS = 0;
			long remoteCounter = 0;

			remoteTS = buf.readLong();
			remoteCounter = buf.readLong();
							
			if(remoteCounter != counter){
				String error = "Expected remote counter to be "+counter+" but it was actually "+remoteCounter+", while TS was "+remoteTS;
				System.out.println(error);
				throw new RuntimeException(error);
			}

			long localTS = System.nanoTime();
			long latency = localTS - remoteTS;
			
			if(latency > maxLatency) maxLatency = latency;
			if(latency < minLatency) minLatency = latency;
			sumLatency += latency;
			totalBytes+=16;
			counter++;
		}
	}
}
