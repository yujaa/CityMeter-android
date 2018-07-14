package uic.hcilab.citymeter.CognitiveTestFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uic.hcilab.citymeter.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Test_EndFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Test_EndFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Test_EndFragment extends android.app.Fragment {

    public Test_EndFragment() {
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
        return inflater.inflate(R.layout.fragment_test__end, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event

}
