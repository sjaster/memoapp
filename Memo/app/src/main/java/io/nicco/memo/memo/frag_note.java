package io.nicco.memo.memo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

public class frag_note extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_note, container, false);

        //v.findViewById(R.id.frag_note_tv).startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.clockwise));

        return v;
    }
}
