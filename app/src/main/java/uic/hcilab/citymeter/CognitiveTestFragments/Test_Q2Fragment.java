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
 * {@link Test_Q1Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Test_Q1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Test_Q2Fragment extends android.app.Fragment {

    public Test_Q2Fragment() {
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
        return inflater.inflate(R.layout.fragment_test__q2, container, false);
    }
}