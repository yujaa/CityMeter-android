package uic.hcilab.citymeter.CognitiveTestFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;

import uic.hcilab.citymeter.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Test_InfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Test_InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Test_InfoFragment extends Fragment {

    RadioGroup q3;
    RadioGroup q3Sub;
    TableRow hide1;
    TableRow hide2;
    TableRow hide3;
    TableRow hide4;

    public Test_InfoFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_test__info, container, false);

        q3 = (RadioGroup)v.findViewById(R.id.testInfoQ3RG);
        q3Sub = (RadioGroup)v.findViewById(R.id.testInfoQ3SubRG);
        hide1 =(TableRow)v.findViewById(R.id.testInfoQ3Row1);
        hide2 =(TableRow)v.findViewById(R.id.testInfoQ3Row2);
        hide3 =(TableRow)v.findViewById(R.id.testInfoQ3Row3);
        hide4 =(TableRow)v.findViewById(R.id.testInfoQ3Row4);


        q3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.testInfoQ3RB1){
                    hide1.setVisibility(View.VISIBLE);
                    hide2.setVisibility(View.VISIBLE);
                }
                else{
                    hide1.setVisibility(View.INVISIBLE);
                    hide2.setVisibility(View.INVISIBLE);
                }
            }
        });

        q3Sub.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.testInfoQ3SubRB1){
                    hide3.setVisibility(View.VISIBLE);
                    hide4.setVisibility(View.VISIBLE);
                }
                else{
                    hide3.setVisibility(View.INVISIBLE);
                    hide4.setVisibility(View.INVISIBLE);
                }
            }
        });

        return v;
    }



}
