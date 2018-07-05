package uic.hcilab.citymeter;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

public class XposureActivity extends TabHost {

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

        //get View for change bar
        final View view = findViewById(android.R.id.content);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Removing layout listener to avoid multiple calls
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                ///Bar
                //PM2.5
                float pm25_bar_loc;
                int pm25_bar_width;
                float pm25_range = 100f; //max - min //ToDo: Toy data
                float pm25_value = 60f;              //ToDo: Toy data
                ImageView pm25_bar = (ImageView) findViewById(R.id.pm25_bar);
                ImageView pm25_thumb = (ImageView) findViewById(R.id.pm25_thumb);
                TextView pm25_thumb_value = (TextView) findViewById(R.id.pm25_value);
                pm25_bar_width = pm25_bar.getWidth();
                pm25_bar_loc=pm25_bar.getX();
                pm25_thumb.setX(pm25_bar_loc+ pm25_bar_width*(pm25_value/pm25_range)-(pm25_thumb.getWidth()/2));
                pm25_thumb_value.setX(pm25_thumb.getX()+pm25_thumb.getWidth());
                pm25_thumb_value.setText(pm25_value+"");

                //day noise
                float day_noise_bar_loc;
                int day_noise_bar_width;
                float day_noise_range = 100f; //max - min //ToDo: Toy data
                float day_noise_value = 20f;              //ToDo: Toy data
                ImageView day_noise_bar = (ImageView) findViewById(R.id.day_noise_bar);
                ImageView day_noise_thumb = (ImageView) findViewById(R.id.day_noise_thumb);
                TextView day_noise_thumb_value = (TextView) findViewById(R.id.day_noise_value);
                day_noise_bar_width = day_noise_bar.getWidth();
                day_noise_bar_loc=day_noise_bar.getX();
                day_noise_thumb.setX(day_noise_bar_loc+ day_noise_bar_width*(day_noise_value/day_noise_range)-(day_noise_thumb.getWidth()/2));
                day_noise_thumb_value.setX(day_noise_thumb.getX()+day_noise_thumb.getWidth());
                day_noise_thumb_value.setText(day_noise_value+"");

                //day noise
                float night_noise_bar_loc;
                int night_noise_bar_width;
                float night_noise_range = 100f; //max - min //ToDo: Toy data
                float night_noise_value = 30f;              //ToDo: Toy data
                ImageView night_noise_bar = (ImageView) findViewById(R.id.night_noise_bar);
                ImageView night_noise_thumb = (ImageView) findViewById(R.id.night_noise_thumb);
                TextView night_noise_thumb_value = (TextView) findViewById(R.id.night_noise_value);
                night_noise_bar_width = night_noise_bar.getWidth();
                night_noise_bar_loc= night_noise_bar.getX();
                night_noise_thumb.setX(night_noise_bar_loc+ night_noise_bar_width*(night_noise_value/night_noise_range)-(night_noise_thumb.getWidth()/2));
                night_noise_thumb_value.setX(night_noise_thumb.getX()+night_noise_thumb.getWidth());
                night_noise_thumb_value.setText(night_noise_value+"");
            }
        });
    }
}
