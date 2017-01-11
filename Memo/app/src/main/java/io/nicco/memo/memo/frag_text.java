package io.nicco.memo.memo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

public class frag_text extends Fragment {

    private ImageView btn_c;
    private ImageView btn_s;
    private EditText et_title;
    private EditText et_body;
    private ScrollView sv_body;

    private NoteText nt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_text, container, false);

        nt = new NoteText(getContext());

        btn_c = (ImageView) v.findViewById(R.id.frag_text_trash);
        btn_s = (ImageView) v.findViewById(R.id.frag_text_save);
        et_title = (EditText) v.findViewById(R.id.frag_text_title);
        et_body = (EditText) v.findViewById(R.id.frag_text_body);
        sv_body = (ScrollView) v.findViewById(R.id.frag_text_scrollview);

        btn_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sv_body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Click", v.toString());
                et_body.requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_body, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        return v;
    }
}
