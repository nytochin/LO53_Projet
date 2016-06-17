package jwabo.lo53_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;


/**
 * Activity CalibrateActivity
 * used for calibrating the positioning system
 */
public class CalibrateActivity extends AppCompatActivity implements OnClickListener, View.OnTouchListener {
    Button button;
    ImageView pin;
    ImageView map;
    TextView coord = null;
    int pinX = 0, pinY = 0, pinZ = 0;
    int pinHeight, pinWidth;
    //"http://172.20.10.14:8080/IPSServer/CalibrationServlet"
    //"http://192.168.1.40/android/index.php";
    String URL;
    String address;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);
        SharedPreferences preferences = getSharedPreferences("preference", Context.MODE_WORLD_WRITEABLE);
        if(preferences.getString("IPSERVER", "")=="" || preferences.getString("PATH", "")=="" || preferences.getString("IPPORT", "")==""){
            Toast toast = Toast.makeText(getApplicationContext(), "Please set address and port first", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            URL = "http://" + preferences.getString("IPSERVER", "0.0.0.0") + ":" + preferences.getString("IPPORT", "8080") + "/" + preferences.getString("PATH", "");
        }
        //URL = "http://"+preferences.getString("IPSERVER", "0.0.0.0")+"/"+preferences.getString("PATH", "");
        //URL = "http://192.168.1.13/android/index.php";
        //"http://172.20.10.14:8080/IPSServer/CalibrationServlet"
        findViewsById();
        button.setOnClickListener(this);
        map.setOnTouchListener(this);
        pinHeight = pin.getHeight();
        pinWidth = pin.getWidth();
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        address = info.getMacAddress();
    }

    private void findViewsById() {
        button = (Button) findViewById(R.id.button);
        coord = (TextView) findViewById(R.id.coordinates);
        map = (ImageView) findViewById(R.id.map);
        pin = (ImageView) findViewById(R.id.pinCalibrate);
    }

    public void onClick(View view) {
        GetXMLTask task = new GetXMLTask();
        task.execute(new String[] { URL });
//        button.setText("CALIBRATING...");
//        button.setClickable(false);
//        button.setBackgroundColor(Color.RED);
    }

    private class GetXMLTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String output = null;
            for (String url : urls) {
                sendRequest(url);
            }
            return output;
        }

        private void sendRequest(String url)  {
            String output;
            try {
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("typeRequest", "findme"));
                postParameters.add(new BasicNameValuePair("bssid", address));
                postParameters.add(new BasicNameValuePair("x", Integer.toString(pinX)));
                postParameters.add(new BasicNameValuePair("y", Integer.toString(pinY)));
                postParameters.add(new BasicNameValuePair("z", Integer.toString(pinZ)));
                HttpPost httppost = new HttpPost(url);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                httppost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse httpResponse = httpClient.execute(httppost);
                HttpEntity httpEntity = httpResponse.getEntity();
                output = EntityUtils.toString(httpEntity);
                System.out.println(output);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String output) {
            coord.setText(output);
            if(output == "TRYAGAIN") {
                GetXMLTask task = new GetXMLTask();
                task.execute(new String[] { URL });
            }
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        pinX = (int)motionEvent.getX()-72;
        pinY = (int)motionEvent.getY()-72*2;
        pin.setPadding(pinX, pinY, 0, 0);
        coord.setText(Integer.toString(pinX)+","+" "+Integer.toString(pinY));
        pin.setVisibility(View.VISIBLE);
        pin.bringToFront();
        return false;
    }
}
