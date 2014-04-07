package com.targetcompid.javanetworkbenchmarks;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractClient {
	public final String id;
	
	private long startTime = 0;
	private long endTime = 0;
	
	int size = 0;
	long counter = 0;		
	int totalBytes = 0;
	long minLatency = Long.MAX_VALUE;
	long maxLatency = Long.MIN_VALUE;
	long sumLatency = 0;
	
	public final static DecimalFormat df = new DecimalFormat("#.####");
	
	
	public AbstractClient(String id){
		this.id = id;
	}
	
	public final long startTimer(){ return startTime = System.nanoTime(); }
	public final long endTimer(){ return endTime = System.nanoTime(); }
	public final long duration(){ return endTime - startTime; }
	
	public Map<String,String> getResults(){
		final Map<String,String> res = new HashMap<String, String>();
		res.put("ID", id);
		res.put("MIN_LATENCY", df.format(minLatency/1000d));
		res.put("MAX_LATENCY", df.format(maxLatency /1000d));
		res.put("AVG_LATENCY", df.format((counter==0? 0 :(sumLatency/counter))/1000d));
		res.put("MICROSECS_PER_BYTE", df.format(totalBytes==0?0:((((double)duration())/1000d)/((double)totalBytes))));
		res.put("MSGS_PER_MILLISEC", df.format(duration()==0?  0 : ((double)counter)/(((double)duration())/1000d/1000d)));
		
		return res;
	}
}
