package jwabo.lo53_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity SettingsActivity
 * used for setting the address of the servlet and the port
 */
public class SettingsActivity extends AppCompatActivity {

    Button ok = null;
    EditText ipServer = null;
    EditText ipPort = null;
    EditText path = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ok = (Button) findViewById(R.id.ok);
        ipServer = (EditText) findViewById(R.id.ipServer);
        ipPort = (EditText) findViewById(R.id.ipPort);
        path = (EditText) findViewById(R.id.path);
        ok.setOnClickListener(okListener);
        SharedPreferences preferences = getSharedPreferences("preference", Context.MODE_WORLD_WRITEABLE);
        ipServer.setText(preferences.getString("IPSERVER", "0.0.0.0"));
        ipPort.setText(preferences.getString("IPPORT", "8080"));
        path.setText(preferences.getString("PATH", " "));
    }

    private View.OnClickListener okListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences preferences = getSharedPreferences("preference", Context.MODE_WORLD_WRITEABLE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("IPSERVER", ipServer.getText().toString());
            editor.putString("IPPORT", ipPort.getText().toString());
            editor.putString("PATH", path.getText().toString());
            editor.commit();
            Intent goIntent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(goIntent);
        }

    };
}
