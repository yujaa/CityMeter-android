package uic.hcilab.citymeter;

import android.content.res.ColorStateList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.Api;

import org.json.simple.JSONObject;

public class StepsActivity extends TabHost implements ApiCallback{

    public double pmNowData;
    public double soundNowData;
    DataAnalysis da =  new DataAnalysis();

    @Override
    public int getContentViewId() {
        return R.layout.activity_steps;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_steps);
        setSupportActionBar(myToolbar);

        new AoTData(StepsActivity.this).execute("value/node/pm25/001e06113107");
        new AoTData(StepsActivity.this).execute("value/nodes");
    }

    @Override
    public void onApiCallback(JSONObject jsonData, String urlApi) {
        //Log.i("my", String.valueOf(jsonData));
        //PM2.5

        if(urlApi.equals("value/node/pm25/001e06113107"))
            getPmData(jsonData);

        else if(urlApi.equals("value/nodes")) {
            getSoundData(jsonData);

            Button pm_btn = (Button)findViewById(R.id.steps_pm25);
            pm_btn.setText(da.getTextOfPM(pmNowData)+'\n'+(int)pmNowData);
            pm_btn.setBackgroundTintList(ColorStateList.valueOf(da.getColorOfPM(pmNowData)));

            Button sound_btn = (Button)findViewById(R.id.steps_sound);
            sound_btn.setText(da.getTextOfSound(soundNowData)+'\n'+(int)soundNowData);
            sound_btn.setBackgroundTintList(ColorStateList.valueOf(da.getColorOfSound(soundNowData)));

            TextView steps_text = (TextView)findViewById(R.id.steps_text1);
            steps_text.setText(da.getStepsToDo(pmNowData));
        }
    }

    private void getPmData(JSONObject nodeValue)
    {
        if(nodeValue.containsKey("001e06113107"))
            pmNowData = Float.parseFloat(nodeValue.get("001e06113107").toString());
    }

    private void getSoundData(JSONObject nodesValue)
    {
        for(Object k: nodesValue.keySet()){
            if(k.toString().equals("001e0610bbff")) {
                soundNowData = Float.parseFloat(((JSONObject)nodesValue.get(k.toString())).get("sound").toString());
            }
        }
    }
}
