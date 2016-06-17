package com.servlet.utilities;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public final class QueryWithContext {

    public static void queryCalibration(PrintWriter out) throws NamingException {
        Context context = null;
        DataSource datasource = null;
        Connection connect = null;
        Statement statement = null;
        try {
            // Get the context and create a connection
            context = new InitialContext();
            datasource = (DataSource) context.lookup("java:/comp/env/jdbc/ips");
            connect = datasource.getConnection();
            // Create the statement to be used to get the results.
            statement = connect.createStatement();
            String query = "SELECT * FROM location";
            // Execute the query and get the result set.
            ResultSet resultSet = statement.executeQuery(query);
            out.println("<strong>Printing result using context file...</strong><br>");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                float x = resultSet.getFloat("x");
                float y = resultSet.getFloat("y");
                float z = resultSet.getFloat("z");
                int bssid = resultSet.getInt("bssid");
                out.println("id: " + id + 
                        ", x: " + x + 
                        ", y: " + y + 
                        ", z: " + z + 
                        ", bssid: " + bssid );
            }
        } catch (SQLException e) { e.printStackTrace(out);
        } finally {
            // Close the connection and release the resources used.
            try { statement.close(); } catch (SQLException e) { e.printStackTrace(out); }
            try { connect.close(); } catch (SQLException e) { e.printStackTrace(out); }
        }
    }
    
    /**
	Add location into database.
	* @param bssid a string 
	* @param x the x coordinate of the location
	* @param y the y coordinate of the location
	* @param z the z coordinate of the location
	*/
    public static void queryCalibrationAddLocation(String bssid, String x, String y, String z) throws NamingException {
        Context context = null;
        DataSource datasource = null;
        Connection connect = null;
        Statement statement = null;
        PrintStream out = null;      
		try {
            // Get the context and create a connection
            context = new InitialContext();
            datasource = (DataSource) context.lookup("java:/comp/env/jdbc/ips");
            connect = datasource.getConnection();
            // Add data to Location table
            statement = connect.createStatement();
            String insert = "INSERT INTO location (bssid, x, y, z) VALUES ('" + bssid + "'," + x + "," + y + "," + z + ");";                        
            // Execute the query and get the result set.
            statement.executeUpdate(insert);
        } catch (SQLException e) { System.out.println(e.getMessage());
        } finally {
            // Close the connection and release the resources used.
            try { statement.close(); } catch (SQLException e) { e.printStackTrace(out); }
            try { connect.close(); } catch (SQLException e) { e.printStackTrace(out); }
        }
    }

    /**
	Get all the waypoints MAC address.
	* @return an array of all the MAC addresses
	*/
	public static  ArrayList<String> queryCalibrationSelectWaypoints() throws NamingException {
		ArrayList<String> waypoints = new ArrayList<String>();		
		Context context = null;
        DataSource datasource = null;
        Connection connect = null;
        Statement statement = null;
        PrintStream out = null;
		try {
            // Get the context and create a connection
            context = new InitialContext();
            datasource = (DataSource) context.lookup("java:/comp/env/jdbc/ips");
            connect = datasource.getConnection();	                    	            
            // Create the statement to be used to get the results.
            statement = connect.createStatement();
            String query = "SELECT mac FROM WAYPOINT";
            // Execute the query and get the result set.
            ResultSet resultSet = statement.executeQuery(query);          
            while (resultSet.next()) {
            	String mac = resultSet.getString("mac");
            	waypoints.add(mac);
            }
        } catch (SQLException e) { e.printStackTrace(out);
        } finally {
            // Close the connection and release the resources used.
            try { statement.close(); } catch (SQLException e) { e.printStackTrace(out); }
            try { connect.close(); } catch (SQLException e) { e.printStackTrace(out); }
        }
		return waypoints;		
	}

	/**
	Add location into database.
	* @param ypmac a string of the MAC address of the waypoint
	* @param x the x coordinate of the location
	* @param y the y coordinate of the location
	* @param z the z coordinate of the location
	* @param value the RSSI value 
	*/
	public static void queryCalibrationAddMeasurement(String ypmac, String x, String y, String z, Double value) throws NamingException {
		Context context = null;
        DataSource datasource = null;
        Connection connect = null;
        Statement statement = null;
        PrintStream out = null;
		try {
            // Get the context and create a connection
            context = new InitialContext();
            datasource = (DataSource) context.lookup("java:/comp/env/jdbc/ips");
            connect = datasource.getConnection();
            // Add data to measurement table
            statement = connect.createStatement();
            String yp_id = "SELECT id FROM WAYPOINT WHERE mac = \"" + ypmac + "\""; 
            ResultSet yp_ids = statement.executeQuery(yp_id);
            yp_ids.next();
            yp_id = yp_ids.getString(1);
            String loc_id = "SELECT id FROM LOCATION WHERE x = " + x + " AND y = " + y + " AND z = "+ z; 
            ResultSet loc_ids = statement.executeQuery(loc_id);
            loc_ids.next();
            loc_id = loc_ids.getString(1);           
            String insert = "INSERT INTO MEASUREMENT (YP_ID, LOC_ID, SS_VALUE) VALUES (" + yp_id + ", " + loc_id + ", " + value + ")";                        
            // Execute the query and get the result set.
            statement.executeUpdate(insert);
            System.out.println("DONE");
        } catch (SQLException e) { System.out.println(e.getMessage());
        } finally {
            // Close the connection and release the resources used.
            try { statement.close(); } catch (SQLException e) { e.printStackTrace(out); }
            try { connect.close(); } catch (SQLException e) { e.printStackTrace(out); }
        }
	}
	
	/**
	Get all the measurements.
	* @return an array of all the fingerprints
	*/
	public static ArrayList<Fingerprint> queryGetAllMeasurement() throws NamingException {
		ArrayList<Fingerprint> fingerprints = new ArrayList<Fingerprint>();		
		Context context = null;
        DataSource datasource = null;
        Connection connect = null;
        Statement statement = null;
        PrintStream out = null;
        try {
            // Get the context and create a connection
            context = new InitialContext();
            datasource = (DataSource) context.lookup("java:/comp/env/jdbc/ips");
            connect = datasource.getConnection();	                      	            
            // Create the statement to be used to get the results.
            statement = connect.createStatement();
            String query = 
            		"SELECT LOC.X, LOC.Y, LOC.Z, MEA.ss_value, YP.MAC "
            		+ "FROM LOCATION LOC, MEASUREMENT MEA, WAYPOINT YP "
            		+ "WHERE LOC.ID = MEA.LOC_ID "
            		+ "AND YP.ID = MEA.YP_ID "
            		+ "ORDER BY LOC.X, LOC.Y, LOC.Z";
            // Execute the query and get the result set.
            ResultSet resultSet = statement.executeQuery(query);          
            float x = -1;
            float y = -1;
            float z = -1;                        
            HashMap<String, Double> hm = null;
            RssiSample rs;
            Location loc = null;            
            boolean firstTime = true;            
            while (resultSet.next()) {
            	if (!(x == resultSet.getFloat("X") && y == resultSet.getFloat("Y") && z == resultSet.getFloat("Z"))){
            		if (!firstTime){
	            		// Add fp to fingerprints
	            		rs = new RssiSample(hm);
	            		Fingerprint fp = new Fingerprint(loc, rs);
	            		fingerprints.add(fp);
            		}            		          		
	            	x = resultSet.getFloat("X");
	            	y = resultSet.getFloat("Y");
	            	z = resultSet.getFloat("Z");
	            	loc = new Location(x, y, z);
	            	hm = new HashMap<String, Double>();
            	}
            	String ypmac = resultSet.getString("mac");
            	float rssi = resultSet.getFloat("ss_value");
            	hm.put(ypmac, (double) rssi);           	           	
            	firstTime = false;
            }
            rs = new RssiSample(hm);
    		Fingerprint fp = new Fingerprint(loc, rs);
    		fingerprints.add(fp);
        } catch (SQLException e) { e.printStackTrace(out);
        } finally {
            // Close the connection and release the resources used.
            try { statement.close(); } catch (SQLException e) { e.printStackTrace(out); }
            try { connect.close(); } catch (SQLException e) { e.printStackTrace(out); }
        }
		return fingerprints;
		
	}
}
