package jwabo.lo53_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Activity MainActivity
 * 
 */
public class MainActivity extends AppCompatActivity {

    Button calibration = null;
    Button settings = null;
    ImageButton findme = null;

    private View.OnClickListener calibrationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent calibrationIntent = new Intent(MainActivity.this, CalibrateActivity.class);
            startActivity(calibrationIntent);
        }
    };
    private View.OnClickListener settingsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingIntent);
        }
    };
    private View.OnClickListener findmeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent findmeIntent = new Intent(MainActivity.this, LocateActivity.class);
            startActivity(findmeIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calibration = (Button) findViewById(R.id.calibration);
        settings = (Button) findViewById(R.id.settings);
        findme = (ImageButton) findViewById(R.id.findme);

        calibration.setOnClickListener(calibrationListener);
        settings.setOnClickListener(settingsListener);
        findme.setOnClickListener(findmeListener);
    }
}
