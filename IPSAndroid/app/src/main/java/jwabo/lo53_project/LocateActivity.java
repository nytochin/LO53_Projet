package jwabo.lo53_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity LocateActivity
 * used for locating the current mobile device
 */
public class LocateActivity extends AppCompatActivity {

    ImageView pin = null;
    TextView coordinates = null;
    int pinX = 0, pinY = 0;
    String URL;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);
        findViewsById();
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        address = info.getMacAddress();
        SharedPreferences preferences = getSharedPreferences("preference", Context.MODE_WORLD_WRITEABLE);
        if (preferences.getString("IPSERVER", "") == ""
                || preferences.getString("PATH", "") == ""
                || preferences.getString("IPPORT", "") == ""){
            Toast toast = Toast.makeText(getApplicationContext(), "Please set the address and the port first", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            URL = "http://"+preferences.getString("IPSERVER", "0.0.0.0")+":"+preferences.getString("IPPORT","8080")+"/"+preferences.getString("PATH","");
            GetXMLTask task = new GetXMLTask();
            task.execute(new String[]{URL});
        }

    }

    private void findViewsById() {
        pin = (ImageView) findViewById(R.id.pinPositioning);
        coordinates = (TextView) findViewById(R.id.coordinatesLocation);
    }

    private class GetXMLTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String output = sendRequest(urls[0]);
            return output;
        }

        private String sendRequest(String url) {
            String output = null;
            try {
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("typeRequest", "findme"));
                postParameters.add(new BasicNameValuePair("bssid", address));
                HttpPost httppost = new HttpPost(url);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                httppost.setEntity(new UrlEncodedFormEntity(postParameters));
                httpClient.execute(httppost);
                HttpResponse httpResponse = httpClient.execute(httppost);
                HttpEntity httpEntity = httpResponse.getEntity();
                output = EntityUtils.toString(httpEntity);
                if (httpEntity != null) {
                    String spinX = httpResponse.getFirstHeader("x").getValue();
                    String spinY = httpResponse.getFirstHeader("y").getValue();
                    pinX = (int) Float.parseFloat(spinX);
                    pinY = (int) Float.parseFloat(spinY);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return output;
        }

        @Override
        protected void onPostExecute(String output) {
            pin.setPadding(pinX, pinY, 0, 0);
            pin.setVisibility(View.VISIBLE);
            pin.bringToFront();
            coordinates.setText(pinX + ", " + pinY);
        }
    }
}
