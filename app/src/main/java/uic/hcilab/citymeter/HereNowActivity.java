package uic.hcilab.citymeter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class HereNowActivity extends TabHost {

    @Override
    public int getContentViewId() {
        return R.layout.activity_here_now;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_here_now);
        setSupportActionBar(myToolbar);
    }
}
