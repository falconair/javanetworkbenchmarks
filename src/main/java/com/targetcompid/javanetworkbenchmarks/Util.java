package com.targetcompid.javanetworkbenchmarks;

import static java.lang.System.out;

import java.util.Map;

public class Util {
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
		sb.append(AbstractClient.df.format(iteration));
		out.println(sb);
	}
	
	private static void attemptGC(){
		//this is just a hint, I know.
		for(int i=0; i< 100; i++)System.gc();
	}
}
