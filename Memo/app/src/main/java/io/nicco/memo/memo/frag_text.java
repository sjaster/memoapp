package io.nicco.memo.memo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

public class frag_text extends Fragment {

    ImageView btn_c, btn_s;
    EditText et_title, et_body;
    ScrollView sv_body;
    View space;

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
        space = v.findViewById(R.id.frag_text_space);

        btn_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_body.setText("");
                et_title.setText("");
                new Utils().toast(getContext(), "Deleted");
            }
        });

        btn_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nt.text = et_body.getText().toString();
                nt.title = et_title.getText().toString();
                new Utils().toast(getContext(), "Saved");
                nt.saveExtra();
            }
        });

        space.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_body.requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_body, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        nt.text = et_body.getText().toString();
        nt.title = et_title.getText().toString();
        et_body.setText("");
        et_title.setText("");
        if (!nt.text.equals(""))
            nt.saveExtra();
    }
}
