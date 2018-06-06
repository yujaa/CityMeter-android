package uic.hcilab.citymeter;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SearchActivity extends TabHost {

    @Override
    public int getContentViewId() {
        return R.layout.activity_search;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_search;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Search");
    }
}
