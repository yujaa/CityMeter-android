package uic.hcilab.citymeter;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

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
        final Button nextBtn = (Button)findViewById(R.id.test_next_btn);
        final ProgressBar pb =(ProgressBar) findViewById(R.id.test_progressBar); // initiate the progress bar
        setSupportActionBar(myToolbar);

        fragmentList.add(new Test_StartFragment());
        fragmentList.add(new Test_InfoFragment());
        fragmentList.add(new Test_Info2Fragment());
        fragmentList.add(new Test_Info3Fragment());
        fragmentList.add(new Test_Q1Fragment());
        fragmentList.add(new Test_Q2Fragment());
        fragmentList.add(new Test_Q3Fragment());
        fragmentList.add(new Test_Q4Fragment());
        fragmentList.add(new Test_Q5Fragment());
        fragmentList.add(new Test_Q6Fragment());
        fragmentList.add(new Test_Q7Fragment());
        fragmentList.add(new Test_Q8Fragment());
        fragmentList.add(new Test_Q9Fragment());
        fragmentList.add(new Test_Q10Fragment());
        fragmentList.add(new Test_Q11Fragment());
        fragmentList.add(new Test_EndFragment());

        pb.setMax(fragmentList.size());

        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.test_frame, fragmentList.get(fIndex)).commit();
            fIndex++;

        }

        nextBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(fIndex ==0)
                    nextBtn.setText("Next");

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.test_frame, fragmentList.get(fIndex));
                ft.commit();
                pb.setProgress(fIndex);
                fIndex++;

                if(fIndex == 16) {
                    nextBtn.setText("Finish");
                    fIndex = 0;
                }
            }
        });




    }



}
