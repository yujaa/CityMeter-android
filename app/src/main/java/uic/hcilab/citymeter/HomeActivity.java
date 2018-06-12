package uic.hcilab.citymeter;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ProgressBar;
import android.widget.TextView;

import uic.hcilab.citymeter.NoiseDetector;

public class HomeActivity extends TabHost {

    private NoiseDetector noiseDetector = new NoiseDetector();

    @Override
    public int getContentViewId() {
        return R.layout.activity_home;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To be removed: just for preview
        final TextView noiseVal = (TextView) findViewById(R.id.noiseValue);
        final ProgressBar noiseBar = (ProgressBar) findViewById(R.id.noiseBar);
        //End of to be removed
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(myToolbar);

        Thread thread = new Thread(new Runnable() {//To run the noise detector in the background
            @Override
            public void run() {
                noiseDetector.noiseDetect();
                while (true) {
                    double dBval = noiseDetector.noiseLevel();
                    Log.i("Recorder", (int)dBval + "");
                    if (dBval > 0 && dBval <= 5000) {
                        //noiseVal.setText( Double.toString(dBval));

                        noiseBar.setProgress((int) dBval);
                    }
                }
            }
        });
        thread.start();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        noiseDetector.stop();
    }
}
