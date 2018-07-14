package uic.hcilab.citymeter;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import uic.hcilab.citymeter.CognitiveTestFragments.*;

import uic.hcilab.citymeter.CognitiveTestFragments.Test_InfoFragment;

public class TestActivity extends TabHost {

    private List<Fragment> fragmentList = new ArrayList<>();
    int fIndex = 0;

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
        Button nextBtn = (Button)findViewById(R.id.test_next_btn);
        setSupportActionBar(myToolbar);

        fragmentList.add(new Test_InfoFragment());
        fragmentList.add(new Test_Info2Fragment());
        fragmentList.add(new Test_Info3Fragment());
        fragmentList.add(new Test_Q1Fragment());
        fragmentList.add(new Test_Q2Fragment());

        //Fragment
        FrameLayout frame = findViewById(R.id.test_frame);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.test_frame, fragmentList.get(fIndex)).commit();
            fIndex++;
        }

        nextBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.test_frame, fragmentList.get(fIndex));
                ft.commit();
                fIndex++;
            }
        });


    }



}
