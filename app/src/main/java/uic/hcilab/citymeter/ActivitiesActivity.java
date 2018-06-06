package uic.hcilab.citymeter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class ActivitiesActivity extends TabHost {

    @Override
    public int getContentViewId() {
        return R.layout.activity_activities;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_activities;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_activities);
        setSupportActionBar(myToolbar);
    }
}
