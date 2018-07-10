package uic.hcilab.citymeter;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class XposureActivity extends TabHost implements ApiCallback{
    HashMap<String, Float> exposureData = new HashMap<String, Float>();

    @Override
    public int getContentViewId() {
        return R.layout.activity_xposure;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_xposure);
        setSupportActionBar(myToolbar);

        new AoTData(XposureActivity.this).execute("exposure");

    }

    @Override
    public void onApiCallback(JSONObject jsonData, String urlApi) {
        //Log.i("my", String.valueOf(jsonData));
        if (urlApi.equals("exposure")) {
            getExposureData(jsonData);

            //PM2.5
            float pm25_bar_loc;
            int pm25_bar_width;
            float pm25_range = 650f; //max - min //ToDo: Toy data
            float pm25_value = (exposureData.get("node_pm25_avg")+ exposureData.get("sensor_pm25_avg"))/2;
            ImageView pm25_bar = (ImageView) findViewById(R.id.pm25_bar);
            ImageView pm25_thumb = (ImageView) findViewById(R.id.pm25_thumb);
            TextView pm25_thumb_value = (TextView) findViewById(R.id.pm25_value);
            pm25_bar_width = pm25_bar.getWidth();
            pm25_bar_loc=pm25_bar.getX();
            pm25_thumb.setX(pm25_bar_width - (pm25_bar_loc+ pm25_bar_width*(pm25_value/pm25_range)-(pm25_thumb.getWidth()/2)));
            pm25_thumb_value.setX(pm25_thumb.getX()+pm25_thumb.getWidth());
            pm25_thumb_value.setText(Math.round(pm25_value)+"");

            //day noise
            float day_noise_bar_loc;
            int day_noise_bar_width;
            float day_noise_range = 90; //max - min //ToDo: Toy data
            float day_noise_value = (exposureData.get("node_sound_avg")+ exposureData.get("sensor_sound_avg"))/2;
            ImageView day_noise_bar = (ImageView) findViewById(R.id.day_noise_bar);
            ImageView day_noise_thumb = (ImageView) findViewById(R.id.day_noise_thumb);
            TextView day_noise_thumb_value = (TextView) findViewById(R.id.day_noise_value);
            day_noise_bar_width = day_noise_bar.getWidth();
            day_noise_bar_loc=day_noise_bar.getX();
            day_noise_thumb.setX(day_noise_bar_width - (day_noise_bar_loc+ day_noise_bar_width*((day_noise_value-35)/day_noise_range)-(day_noise_thumb.getWidth()/2)));
            day_noise_thumb_value.setX(day_noise_thumb.getX()+day_noise_thumb.getWidth());
            day_noise_thumb_value.setText(Math.round(day_noise_value)+"");

            //day noise
            float night_noise_bar_loc;
            int night_noise_bar_width;
            float night_noise_range = 90f; //max - min //ToDo: Toy data
            float night_noise_value = 40f;              //ToDo: Toy data
            ImageView night_noise_bar = (ImageView) findViewById(R.id.night_noise_bar);
            ImageView night_noise_thumb = (ImageView) findViewById(R.id.night_noise_thumb);
            TextView night_noise_thumb_value = (TextView) findViewById(R.id.night_noise_value);
            night_noise_bar_width = night_noise_bar.getWidth();
            night_noise_bar_loc= night_noise_bar.getX();
            night_noise_thumb.setX(night_noise_bar_width-(night_noise_bar_loc+ night_noise_bar_width*((night_noise_value-35)/night_noise_range)-(night_noise_thumb.getWidth()/2)));
            night_noise_thumb_value.setX(night_noise_thumb.getX()+night_noise_thumb.getWidth());
            night_noise_thumb_value.setText(Math.round(night_noise_value)+"");
        }
    }

    private void getExposureData(JSONObject nodeValue)
    {

        if(nodeValue.containsKey("node_pm25_avg"))
            exposureData.put("node_pm25_avg",Float.parseFloat(nodeValue.get("node_pm25_avg").toString()));
        if(nodeValue.containsKey("sensor_pm25_avg"))
            exposureData.put("sensor_pm25_avg",Float.parseFloat(nodeValue.get("sensor_pm25_avg").toString()));
        if(nodeValue.containsKey("node_sound_avg"))
            exposureData.put("node_sound_avg",Float.parseFloat(nodeValue.get("node_sound_avg").toString()));
        if(nodeValue.containsKey("sensor_sound_avg"))
            exposureData.put("sensor_sound_avg",Float.parseFloat(nodeValue.get("sensor_sound_avg").toString()));

        Log.i("my",(Float.parseFloat(nodeValue.get("sensor_sound_avg").toString()))+"");
    }
}
