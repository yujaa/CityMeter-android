package uic.hcilab.citymeter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AddCoUserActivity extends TabHost {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_co_user);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_add_co_user;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }
}
