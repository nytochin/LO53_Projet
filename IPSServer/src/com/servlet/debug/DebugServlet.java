package com.servlet.debug;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.servlet.utilities.Fingerprint;
import com.servlet.utilities.Location;
import com.servlet.utilities.QueryWithContext;
import com.servlet.utilities.RssiSample;

/**
 * Servlet implementation class PositioningServlet
 */
@WebServlet("/DebugServlet")
public class DebugServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	DatagramPacket packet;
	private float timeOut = 5.0f;
	private long start;
	private Map<String, Double> mapGotchas = new HashMap<String, Double>();
	
	private ArrayList<String> currentDevices = new ArrayList<String>();
	/**
     * @see HttpServlet#HttpServlet()
     */
    public DebugServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: Positioning");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
					
		response.addHeader("x", Double.toString(100));
		response.addHeader("y", Double.toString(100));
		response.addHeader("z", Double.toString(0));			
							
		System.out.println("receivedDEBUG");
				
			 
			
	}
	
	public Location computeLocation(ArrayList<Fingerprint> fingerprintsArray, RssiSample sample){
		double x = 0;
		double y = 0;
		double z = 0;
		
		ArrayList<Fingerprint> fingerprints = findThreeNearestLocations(fingerprintsArray, sample);
		
		double w0, w1, w2;
		w0 = 1.0/rssi_distance(fingerprints.get(0).getMeasurement(), sample);
		w1 = 1.0/rssi_distance(fingerprints.get(1).getMeasurement(), sample);
		w2 = 1.0/rssi_distance(fingerprints.get(2).getMeasurement(), sample);
		
		x = fingerprints.get(0).getLocation().getX()*w0
				+ fingerprints.get(1).getLocation().getX()*w1
				+ fingerprints.get(2).getLocation().getX()*w2;
		
		y = fingerprints.get(0).getLocation().getY()*w0
				+ fingerprints.get(1).getLocation().getY()*w1
				+ fingerprints.get(2).getLocation().getY()*w2;
		
		return new Location(x,y,z);
		
	}
	
	public ArrayList<Fingerprint> findThreeNearestLocations(ArrayList<Fingerprint> fingerprints, RssiSample sample){
		
		HashMap<Fingerprint, Double> hm = new HashMap<Fingerprint, Double>();		
		ArrayList<Fingerprint> fpsorted = new ArrayList<Fingerprint>();	
		
		for (Fingerprint fp : fingerprints){
			double d = rssi_distance(fp.getMeasurement(), sample);
			hm.put(fp, d);
		}
		
		// Sort in decreasing order
		
		int count = 1;
		LinkedHashMap<Fingerprint, Double> lhm = sortHashMapByValues(hm);
		
		for (Entry<Fingerprint, Double> e : lhm.entrySet()){			
			fpsorted.add(e.getKey());
			if (count == 3){
				break;
			}
			count++;
		}				
		return fpsorted;		
	}
	
	public LinkedHashMap<Fingerprint, Double> sortHashMapByValues(
	        HashMap<Fingerprint, Double> passedMap) {
	    List<Fingerprint> mapKeys = new ArrayList<Fingerprint>(passedMap.keySet());
	    List<Double> mapValues = new ArrayList<Double>(passedMap.values());
	    Collections.sort(mapValues);

	    LinkedHashMap<Fingerprint, Double> sortedMap =
	        new LinkedHashMap<Fingerprint, Double>();

	    Iterator<Double> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	        double val = valueIt.next();
	        Iterator<Fingerprint> keyIt = mapKeys.iterator();

	        while (keyIt.hasNext()) {
	            Fingerprint key = keyIt.next();
	            double comp1 = passedMap.get(key);
	            double comp2 = val;

	            if (comp1 == comp2) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    return sortedMap;
	}
	
	public double rssi_distance(RssiSample s1, RssiSample s2){
		double d = 0;
		for (Entry<String, Double> e : s1.getRssi().entrySet()){
			if (s2.getRssi().containsKey(e.getKey())){
				d += Math.pow(e.getValue() - s2.getRssi().get(e.getKey()), 2);
			} else {
				d += Math.pow(e.getValue() + 95, 2);
			}
		}
		
		for (Entry<String, Double> e : s2.getRssi().entrySet()){
			if (!s1.getRssi().containsKey(e.getKey())){				
				d += Math.pow(e.getValue() + 95, 2);
			}
		}
		return Math.sqrt(d);		
	}

}
