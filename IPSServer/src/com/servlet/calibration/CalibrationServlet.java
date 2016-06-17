package com.servlet.calibration;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

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

import com.servlet.utilities.QueryWithContext;
import com.servlet.utilities.UrlAddress;

/**
 * Servlet implementation class CalibrationServlet
 */
@WebServlet("/CalibrationServlet")
public class CalibrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	       
    /**
     * @see HttpServlet#HttpServlet()
     * Default constructor of a servlet
     */
    public CalibrationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		response.getWriter().append("Served at: Calibration");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * When a post request is received, this function is called and process it.
	 * Forward request from mobile to waypoints, then retrieve data from waypoints and store into database
	 * as calibration data.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		// Get the (x, y, z) coordinates sent by the mobile device
		String bssid = request.getParameter("bssid");
		String x = request.getParameter("x");
		String y = request.getParameter("y");
		String z = request.getParameter("z");
					 
		// currentDevices.add(bssid);
		// DB : Add the location of the current mobile device
		try {
			QueryWithContext.queryCalibrationAddLocation(bssid, x, y, z);
		} catch (NamingException e) {
			e.printStackTrace();
		}	 				
		
		//String bssidTemp = "5a:af:75:53:5e:19";
		//String bssidTemp = "0c:30:21:a6:f1:18";
		//String bssidTemp = "5c:2e:59:f5:52:2a";

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
				
		// Add measurements into db
		try{
			QueryWithContext.queryCalibrationAddMeasurement(ap_mac1, x, y, z, avg_rssi1 );
			QueryWithContext.queryCalibrationAddMeasurement(ap_mac2, x, y, z, avg_rssi2 );
			QueryWithContext.queryCalibrationAddMeasurement(ap_mac3, x, y, z, avg_rssi3 );					
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
		// send back a CALIBRATION POINT DONE response to the mobile device
		PrintWriter outResponse = response.getWriter();
		outResponse.write("CALIBRATION POINT DONE");
		outResponse.close();	
			
	} 
			 
	
	
}
