
package com.servlet.utilities;

/**
 * Class Fingerprint
 * 
 */

public class Fingerprint {
	private Location location; /** location */ 
	private RssiSample measurement; /** measurements associated with the location */ 
	
	Fingerprint(Location l, RssiSample m){
		setLocation(l);
		setMeasurement(m);
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public RssiSample getMeasurement() {
		return measurement;
	}

	public void setMeasurement(RssiSample measurement) {
		this.measurement = measurement;
	}
}
