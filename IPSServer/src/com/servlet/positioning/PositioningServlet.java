package com.servlet.positioning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.servlet.utilities.Fingerprint;
import com.servlet.utilities.Location;
import com.servlet.utilities.QueryWithContext;
import com.servlet.utilities.RssiSample;
import com.servlet.utilities.UrlAddress;

/**
 * Servlet PositioningServlet
 * This servlet is used for locating the mobile device.
 */
@WebServlet("/PositioningServlet")
public class PositioningServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Map<String, Double> mapGotchas = new HashMap<String, Double>(); /** Map containing MAC address of waypoints and the RSSI values */  	
	
	/**
     * @see HttpServlet#HttpServlet()
     * Default constructor of a servlet
     */
    public PositioningServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: Positioning");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * When a post request is received, this function is called and process it.
	 * Forward request from mobile to waypoints, then retrieve data from waypoints and compute the location
	 * and send the location to mobile.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Positioning Servlet has received something.");
		response.setContentType("text/plain");
		
		String bssid = request.getParameter("bssid");	
		
		// request URL building		
		String url1 = "http://"+UrlAddress.ROUTER1.url+":"+UrlAddress.ROUTER1.port+"?mobile_mac="+bssid.toLowerCase();
		String url2 = "http://"+UrlAddress.ROUTER2.url+":"+UrlAddress.ROUTER2.port+"?mobile_mac="+bssid.toLowerCase();
		String url3 = "http://"+UrlAddress.ROUTER3.url+":"+UrlAddress.ROUTER3.port+"?mobile_mac="+bssid.toLowerCase();
		
		// Sending
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet APrequest1 = new HttpGet(url1);
		HttpGet APrequest2 = new HttpGet(url2);
		HttpGet APrequest3 = new HttpGet(url3);
		HttpResponse APresponse1 = client.execute(APrequest1);
		HttpResponse APresponse2 = client.execute(APrequest2);
		HttpResponse APresponse3 = client.execute(APrequest3);

		BufferedReader rd1 = new BufferedReader(
				new InputStreamReader(APresponse1.getEntity().getContent()));
		BufferedReader rd2 = new BufferedReader(
				new InputStreamReader(APresponse2.getEntity().getContent()));
		BufferedReader rd3 = new BufferedReader(
				new InputStreamReader(APresponse3.getEntity().getContent()));
		
		// JSON String 
		String line = "";
		StringBuffer result1 = new StringBuffer();
		while ((line = rd1.readLine()) != null) {
			result1.append(line);
		}
		StringBuffer result2 = new StringBuffer();
		while ((line = rd2.readLine()) != null) {
			result2.append(line);
		}
		StringBuffer result3 = new StringBuffer();
		while ((line = rd3.readLine()) != null) {
			result3.append(line);
		}
		
		// Debug
		System.out.println(result1.toString());
		System.out.println(result2.toString());
		System.out.println(result3.toString());
		
		final JSONObject obj1 = new JSONObject(result1.toString());
		final JSONObject obj2 = new JSONObject(result2.toString());
		final JSONObject obj3 = new JSONObject(result3.toString());

	    final JSONArray infosignal1 = obj1.getJSONArray("infosignal");
	    final JSONObject infos1 = infosignal1.getJSONObject(0);
	    			    			
	    // Data retrieval
		String ap_mac1 = infos1.getString("ap_mac");
		String mobile_mac1 = infos1.getString("mobile_mac");
		int nbSamples1 = infos1.getInt("number_of_samples");
		double avg_rssi1 = infos1.getDouble("avg_rssi_value");
			
		final JSONArray infosignal2 = obj2.getJSONArray("infosignal");
	    final JSONObject infos2 = infosignal2.getJSONObject(0);
	    			    			      
		String ap_mac2 = infos2.getString("ap_mac");
		String mobile_mac2 = infos2.getString("mobile_mac");
		int nbSamples2 = infos2.getInt("number_of_samples");
		double avg_rssi2 = infos2.getDouble("avg_rssi_value");
		
		final JSONArray infosignal3 = obj3.getJSONArray("infosignal");
	    final JSONObject infos3 = infosignal3.getJSONObject(0);
	    			    			      
		String ap_mac3 = infos3.getString("ap_mac");
		String mobile_mac3 = infos3.getString("mobile_mac");
		int nbSamples3 = infos3.getInt("number_of_samples");
		double avg_rssi3 = infos3.getDouble("avg_rssi_value");
		
		System.out.println(ap_mac1 + " " + mobile_mac1 + " " + nbSamples1 + " " + avg_rssi1);
		System.out.println(ap_mac2 + " " + mobile_mac2 + " " + nbSamples2 + " " + avg_rssi2);
		System.out.println(ap_mac3 + " " + mobile_mac3 + " " + nbSamples3 + " " + avg_rssi3);
							
		// add data to mapGotchas
		mapGotchas.put(ap_mac1, avg_rssi1);
		mapGotchas.put(ap_mac2, avg_rssi2);
		mapGotchas.put(ap_mac3, avg_rssi3);
		
		// Compute location
		RssiSample rssiSample= new RssiSample(mapGotchas);							
		ArrayList<Fingerprint> fingerprints = new ArrayList<Fingerprint>();
		// Query to DB : Get all RSSI Values for each location (from calibration)
		try {
			fingerprints = QueryWithContext.queryGetAllMeasurement();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		// Compute the location and sending back to mobile device
		Location mobileLocation = computeLocation(fingerprints, rssiSample);							
		response.addHeader("x", Double.toString(mobileLocation.getX()));
		response.addHeader("y", Double.toString(mobileLocation.getY()));
		response.addHeader("z", Double.toString(mobileLocation.getZ()));
		
		// send back a TRYAGAIN response to the mobile device
		PrintWriter outResponse = response.getWriter();
		outResponse.write("TRYAGAIN");
		outResponse.close();
		// initialize start time											 	
	}
	
	/**
	Compute the location from distributions of RSSI values.
	* @param fingerprintsArray an array of fingerprints
	* @param sample a RssiSample
	* @return the location of the mobile device
	*/
	
	public Location computeLocation(ArrayList<Fingerprint> fingerprintsArray, RssiSample sample){
		double x = 0;
		double y = 0;
		double z = 0;
		double w0, w1, w2;
		// Get the three nearest locations from the current mobile device (tri-lateration will be used)
		ArrayList<Fingerprint> fingerprints = findThreeNearestLocations(fingerprintsArray, sample);		
		// Computation of the inverse of the RSSI distance between each location and the current mobile device
		w0 = 1.0/rssi_distance(fingerprints.get(0).getMeasurement(), sample);
		w1 = 1.0/rssi_distance(fingerprints.get(1).getMeasurement(), sample);
		w2 = 1.0/rssi_distance(fingerprints.get(2).getMeasurement(), sample);
		// Computation of x and y coordinates (z is not modified)
		x = fingerprints.get(0).getLocation().getX()*w0
				+ fingerprints.get(1).getLocation().getX()*w1
				+ fingerprints.get(2).getLocation().getX()*w2;
		
		y = fingerprints.get(0).getLocation().getY()*w0
				+ fingerprints.get(1).getLocation().getY()*w1
				+ fingerprints.get(2).getLocation().getY()*w2;
		return new Location(x,y,z);		
	}
	
	/**
	Find the three nearest locations from the current position according to the RSSI distances
	* @param fingerprints an array of fingerprints
	* @param sample a RssiSample
	* @return the location of the mobile device
	*/
	public ArrayList<Fingerprint> findThreeNearestLocations(ArrayList<Fingerprint> fingerprints, RssiSample sample){		
		HashMap<Fingerprint, Double> hm = new HashMap<Fingerprint, Double>(); 	// HashMap containing (fingerprint ; rssi distance)
		ArrayList<Fingerprint> fpsorted = new ArrayList<Fingerprint>();			
		// Computation of rssi_distance for each one of the waypointss
		for (Fingerprint fp : fingerprints){
			double d = rssi_distance(fp.getMeasurement(), sample);
			hm.put(fp, d);
		}		
		// Sort in decreasing order of values
		int count = 1;
		LinkedHashMap<Fingerprint, Double> lhm = sortHashMapByValues(hm);
		// only add in fpsorted the three nearest locations (or the three with the biggest rssi_distance value)
		for (Entry<Fingerprint, Double> e : lhm.entrySet()){			
			fpsorted.add(e.getKey());
			if (count == 3){
				break;
			}
			count++;
		}				
		return fpsorted;		
	}
	
	/**
	Sort a hash map in decreasing order
	* @param passedMap a HashMap<Fingerprint, double>
	* @return a LinkedHashMap in the right order
	*/
	public LinkedHashMap<Fingerprint, Double> sortHashMapByValues(HashMap<Fingerprint, Double> passedMap) {
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
	
	/**
	Compute the rssi_distance between two RssiSamples
	* @param s1 a RssiSample
	* @param s2 a RssiSample
	* @return a double value corresponding to the rssi_distance
	*/
	public double rssi_distance(RssiSample s1, RssiSample s2){
		double d = 0;
		for (Entry<String, Double> e : s1.getRssi().entrySet()){
			if (s2.getRssi().containsKey(e.getKey())){
				d += e.getValue() - s2.getRssi().get(e.getKey());
			} else {
				d += e.getValue() + 95;
			}
		}		
		for (Entry<String, Double> e : s2.getRssi().entrySet()){
			if (!s1.getRssi().containsKey(e.getKey())){				
				d += e.getValue() + 95;
			}
		}
		return Math.sqrt(d);				
	}
}
