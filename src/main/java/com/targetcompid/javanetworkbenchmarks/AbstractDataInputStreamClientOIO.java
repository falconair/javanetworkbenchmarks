package com.targetcompid.javanetworkbenchmarks;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

public class AbstractDataInputStreamClientOIO extends AbstractClient{

	public AbstractDataInputStreamClientOIO(String id) {
		super(id);
	}

	public void process(DataInputStream input) throws IOException{
		try{
			while(true){
				long remoteTS = input.readLong();
				long remoteCounter = input.readLong();
				
				if(remoteCounter != counter){
					String error = "Expected remote counter to be "+counter+" but it was actually "+remoteCounter;
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
		catch(EOFException e){
			//EOF reached, how else can this be detected?
		}
	}
}
