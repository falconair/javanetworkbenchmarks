package com.targetcompid.javanetworkbenchmarks;

import java.nio.ByteBuffer;

public class AbstractNIOClient extends AbstractClient{
	
	private final ByteBuffer internalBufferBB = ByteBuffer.allocate(8);

	public AbstractNIOClient(String id){
		super(id);
	}
	
	public void process(int size, ByteBuffer buf){
		//if internal buffer is empty and buf is empty, end loop
		//if internal buffer is empty and buf contains only 1 val, move buf to internal buffer, end loop
		//if internal buffer is empty and buf contains atleast 2 'long' vals, read them and loop
		//if internal buffer is full and buf contains atleast 1 'long val, read both and loop
		while(buf.remaining() > 0){

			long remoteTS = 0;
			long remoteCounter = 0;

			if(internalBufferBB.position() == 0 && buf.remaining() == 0){
				break;
			}
			
			if(internalBufferBB.position() == 0 && buf.remaining() == 8){
				//buf.flip();
				long longVal= buf.getLong();
				internalBufferBB.putLong(longVal);
				break;
			}			
			if(internalBufferBB.position() == 0 && buf.remaining() >= 16){
				//buf.flip();
				remoteTS = buf.getLong();
				remoteCounter = buf.getLong();
			}
			
			if(internalBufferBB.position() == 8 && buf.remaining() >= 8){
				internalBufferBB.flip();
				remoteTS = internalBufferBB.getLong();
				internalBufferBB.clear();
				//buf.flip();
				remoteCounter = buf.getLong();
			}
			
			if(remoteCounter != counter){
				String error = "Expected remote counter to be "+counter+" but it was actually "+remoteCounter;
				System.out.println(error);
				throw new RuntimeException(error);
			}
			
			long localTS = System.nanoTime();
			long latency = localTS - remoteTS;
			
			//out.println(String.format("latency:%s, remoteTS:%s, localTS:%s", latency, remoteTS, localTS));
			
			if(latency > maxLatency) maxLatency = latency;
			if(latency < minLatency) minLatency = latency;
			sumLatency += latency;
			totalBytes+=16;
			counter++;
		}
	}
}
