package uic.hcilab.citymeter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public abstract class TabHost extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    protected BottomNavigationView navigation;


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.navigation_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.navigation_activities:
                startActivity(new Intent(this, ActivitiesActivity.class));
                break;
            case R.id.navigation_summary:
                startActivity(new Intent(this, SummaryActivity.class));
                break;
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        BottomNavigationViewHelper.removeShiftMode(navigation);//disable BottomNavigationView shift mode
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void updateNavigationBarState(){
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId) {
        MenuItem item = navigation.getMenu().findItem(itemId);
        item.setChecked(true);
    }

    public abstract int getContentViewId();
    public abstract int getNavigationMenuItemId();

}
