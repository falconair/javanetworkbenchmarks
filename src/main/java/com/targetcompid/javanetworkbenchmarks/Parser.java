package com.targetcompid.javanetworkbenchmarks;


import static java.lang.System.out;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;



final class Parser{
	public final static DecimalFormat df = new DecimalFormat("#.####");
	//Not thread safe

	private final String id;

	//One of the following is used, depending on the implementation
	private final byte[] internalBufferBA = new byte[8];
	private final ByteBuffer internalBufferBB = ByteBuffer.allocate(8);
	
	private int size = 0;
	private long counter = 0;		
	private int totalBytes = 0;
	private long minLatency = Long.MAX_VALUE;
	private long maxLatency = Long.MIN_VALUE;
	private long sumLatency = 0;
	
	private long startTime = 0;
	private long endTime = 0;

	public Parser(String id){
		this.id = id;
	}
	
	public long startTimer(){ return startTime = System.nanoTime(); }
	public long endTimer(){ return endTime = System.nanoTime(); }
	public long duration(){ return endTime - startTime; }
	

	public void process(InputStream input) throws IOException{
		while(true){
			size = input.read(internalBufferBA, 0, 8);
			if(size == -1) break;
			long remoteTS = toLong(internalBufferBA);

			size = input.read(internalBufferBA, 0, 8);
			if(size == -1) break;
			long remoteCounter = toLong(internalBufferBA);
			
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

	
	public Map<String,String> getResults(){
		final Map<String,String> res = new HashMap<String, String>();
		res.put("ID", id);
		res.put("MIN_LATENCY", Parser.df.format(minLatency/1000d));
		res.put("MAX_LATENCY", Parser.df.format(maxLatency /1000d));
		res.put("AVG_LATENCY", Parser.df.format((counter==0? 0 :(sumLatency/counter))/1000d));
		res.put("MICROSECS_PER_BYTE", Parser.df.format(totalBytes==0?0:((((double)duration())/1000d)/((double)totalBytes))));
		res.put("MSGS_PER_MILLISEC", Parser.df.format(duration()==0?  0 : ((double)counter)/(((double)duration())/1000d/1000d)));
		
		return res;
	}
	
	public static long toLong(byte[] data) {
	    //if (data == null || data.length != 8) return 0x0;
	    // ----------
	    return (long)(
	            // (Below) convert to longs before shift because digits
	            //         are lost with ints beyond the 32-bit limit
	            (long)(0xff & data[0]) << 56  |
	            (long)(0xff & data[1]) << 48  |
	            (long)(0xff & data[2]) << 40  |
	            (long)(0xff & data[3]) << 32  |
	            (long)(0xff & data[4]) << 24  |
	            (long)(0xff & data[5]) << 16  |
	            (long)(0xff & data[6]) << 8   |
	            (long)(0xff & data[7]) << 0
	            );
	}
	
 
	 
	private static void printResult(Map<String, String> result, int iteration) {
		
		StringBuilder sb = new StringBuilder();
		sb.append(result.get("ID")).append(",");
		sb.append(result.get("MIN_LATENCY")).append(",");
		sb.append(result.get("MAX_LATENCY")).append(",");
		sb.append(result.get("AVG_LATENCY")).append(",");
		sb.append(result.get("MICROSECS_PER_BYTE")).append(",");
		sb.append(result.get("MSGS_PER_MILLISEC")).append(",");
		sb.append(result.get("TCP_NO_DELAY")).append(",");
		sb.append(result.get("BUFFER_SIZE")).append(",");
		sb.append(Parser.df.format(iteration));
		out.println(sb);
	}
	
	private static void attemptGC(){
		//this is just a hint, I know.
		for(int i=0; i< 100; i++)System.gc();
	}

}