package uic.hcilab.citymeter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class SummaryActivity extends TabHost {

    @Override
    public int getContentViewId() {
        return R.layout.activity_summary;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_summary;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_summary);
        setSupportActionBar(myToolbar);
    }
}
