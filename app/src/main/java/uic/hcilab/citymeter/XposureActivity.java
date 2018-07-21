package uic.hcilab.citymeter;

import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class XposureActivity extends TabHost implements ApiCallback{
    HashMap<String, Float> exposureData = new HashMap<String, Float>();
    DataAnalysis da = new DataAnalysis();
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
            float pm25_value = (exposureData.get("node_pm25_avg")+ exposureData.get("sensor_pm25_avg"))/2;
            int pm25_level [] = {50, 101, 151, 201, 301, 501};

            ImageView pm25_bar = (ImageView) findViewById(R.id.pm25_bar);
            ConstraintLayout pm25_thumb = (ConstraintLayout) findViewById(R.id.pm25_thumb);
            TextView pm25_thumb_value = (TextView) findViewById(R.id.pm25_value);
            int pm25_bar_width = pm25_bar.getWidth();
            float pm25_bar_loc=pm25_bar.getX();

            double pos = da.getPosOnBar(pm25_value, pm25_level, 6);
            //pm25_thumb.setX((float)(pm25_bar_loc+ pm25_bar_width- (pos * pm25_bar_width /6)-(pm25_thumb.getWidth()/2)));
            setAnimation(pm25_thumb.getX(), (float)(pm25_bar_loc+ pm25_bar_width- (pos * pm25_bar_width /6)-(pm25_thumb.getWidth()/2)), pm25_thumb.getTop()-pm25_thumb.getHeight()/2, pm25_thumb.getTop()-pm25_thumb.getHeight()/2, 1000, pm25_thumb);
            pm25_thumb_value.setText(Math.round(pm25_value)+"");

            //day noise
            float day_noise_value = (exposureData.get("node_sound_avg")+ exposureData.get("sensor_sound_avg"))/2;
            int day_noise_level [] = {55, 65, 75, 85};//{80, 90, 100, 110, 130, 160};

            ImageView day_noise_bar = (ImageView) findViewById(R.id.day_noise_bar);
            ConstraintLayout day_noise_thumb = (ConstraintLayout) findViewById(R.id.day_noise_thumb);
            TextView day_noise_thumb_value = (TextView) findViewById(R.id.day_noise_value);
            int day_noise_bar_width = day_noise_bar.getWidth();
            float day_noise_bar_loc=day_noise_bar.getX();

            pos = da.getPosOnBar(day_noise_value, day_noise_level, 4);
            Log.i("myy", pos+"");
            //day_noise_thumb.setX((float)(day_noise_bar_loc+ day_noise_bar_width - (pos * day_noise_bar_width / 4)-(day_noise_thumb.getWidth()/2)));
            //day_noise_thumb_value.setX(day_noise_thumb.getX()+day_noise_thumb.getWidth());
            setAnimation(day_noise_thumb.getX(), (float)(day_noise_bar_loc+ day_noise_bar_width- (pos * day_noise_bar_width /6)-(day_noise_thumb.getWidth()/2)), day_noise_thumb.getTop()-day_noise_thumb.getHeight()/2, day_noise_thumb.getTop()-day_noise_thumb.getHeight()/2, 1000, day_noise_thumb);
            day_noise_thumb_value.setText(Math.round(day_noise_value)+"");

            //night noise
            float night_noise_value = 40f;              //ToDo: Toy data
            int night_noise_level [] = {55, 65, 75, 85};//{80, 90, 100, 110, 130, 160};

            ImageView night_noise_bar = (ImageView) findViewById(R.id.night_noise_bar);
            ConstraintLayout night_noise_thumb = (ConstraintLayout) findViewById(R.id.night_noise_thumb);
            TextView night_noise_thumb_value = (TextView) findViewById(R.id.night_noise_value);
            int night_noise_bar_width = night_noise_bar.getWidth();
            float night_noise_bar_loc= night_noise_bar.getX();

            pos = da.getPosOnBar(night_noise_value, night_noise_level, 4);
            //night_noise_thumb.setX((float)(night_noise_bar_loc+ night_noise_bar_width - (pos * night_noise_bar_width / 4)-(night_noise_thumb.getWidth()/2)));
            //night_noise_thumb_value.setX(night_noise_thumb.getX()+night_noise_thumb.getWidth());
            setAnimation(night_noise_thumb.getX(), (float)(night_noise_bar_loc+ night_noise_bar_width- (pos * night_noise_bar_width /6)-(night_noise_thumb.getWidth()/2)), night_noise_thumb.getTop()-night_noise_thumb.getHeight()/2, night_noise_thumb.getTop()-night_noise_thumb.getHeight()/2, 1000, night_noise_thumb);
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

    private void setAnimation(float fromX, float toX, float fromY,  float toY, int duration, View view)
    {
        TranslateAnimation animation = new TranslateAnimation(fromX, toX, fromY, toY);
        animation.setDuration(duration);
        animation.setFillAfter(true);

        view.startAnimation(animation);
    }
}
