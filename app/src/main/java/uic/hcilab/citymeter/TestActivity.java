package uic.hcilab.citymeter;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import uic.hcilab.citymeter.CognitiveTestFragments.*;

import uic.hcilab.citymeter.CognitiveTestFragments.Test_InfoFragment;

public class TestActivity extends TabHost {

    private static final int CONTENT_VIEW_ID = 10101010;

    @Override
    public int getContentViewId() {
        return R.layout.activity_test;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_test;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_test);
        setSupportActionBar(myToolbar);

        //Fragment
        FrameLayout frame = findViewById(R.id.test_frame);
        setContentView(frame, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        if (savedInstanceState == null) {
            Fragment newFragment = new Test_InfoFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(CONTENT_VIEW_ID, newFragment).commit();
        }
    }



}
