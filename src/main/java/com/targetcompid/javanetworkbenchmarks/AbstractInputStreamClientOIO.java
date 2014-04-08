package com.targetcompid.javanetworkbenchmarks;

import static java.lang.System.out;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractInputStreamClientOIO extends AbstractClient{
	
	private final byte[] internalBuffer = new byte[8];

	public AbstractInputStreamClientOIO(String id) {
		super(id);
	}

	public void process(InputStream input) throws IOException{
		while(true){
			size = input.read(internalBuffer, 0, 8);
			if(size == -1) break;
			long remoteTS = Util.toLong(internalBuffer);

			size = input.read(internalBuffer, 0, 8);
			if(size == -1) break;
			long remoteCounter = Util.toLong(internalBuffer);
			//out.println("Client counter:"+remoteCounter+", time:"+remoteTS);
			
			if(remoteCounter != counter){
				String error = "Expected remote counter to be "+counter+" but it was actually "+remoteCounter;
				out.println(error);
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
