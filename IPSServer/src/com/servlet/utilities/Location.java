package com.servlet.utilities;

/**
 * Class Location
 * 
 */

public class Location {
	private double x; /** x coordinate of the location */ 
	private double y; /** y coordinate of the location */ 
	private double z; /** z coordinate of the location */ 
	
	public Location (double x, double y, double z){
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
}
