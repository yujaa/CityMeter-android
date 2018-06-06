package uic.hcilab.citymeter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
        getSupportActionBar().setTitle("Summary");
    }
}
