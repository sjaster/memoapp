package io.nicco.memo.memo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class frag_settings extends Fragment {
    
    Switch default_cam;
    Button btn_del_all;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);

        default_cam = (Switch) v.findViewById(R.id.frag_settings_default_cam);
        default_cam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                new Utils().putPref(getContext(), Main.PREF_CAM_DEF, String.valueOf(b));
            }
        });
        default_cam.setChecked(Boolean.parseBoolean(new Utils().getPref(getContext(), Main.PREF_CAM_DEF)));

        btn_del_all = (Button) v.findViewById(R.id.frag_settings_btn_delete_all);
        btn_del_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Utils().rm(getContext().getFilesDir().toString());
                new Utils().toast(getContext(), "All notes deleted");
            }
        });



        return v;
    }
}
