package com.servlet.utilities;

import java.util.HashMap;
import java.util.Map;

/**
 * Class RssiSample
 * 
 */

public class RssiSample {
	private Map<String, Double> rssi; /** Map containing the MAC address of the waypoint and the RSSI value associated with it */ 
	
	public RssiSample(Map<String, Double> mapGotchas){
		this.rssi = mapGotchas;
	}

	public Map<String, Double> getRssi() {
		return rssi;
	}

	public void setRssi(HashMap<String, Double> rssi) {
		this.rssi = rssi;
	}
}
