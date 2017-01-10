package io.nicco.memo.memo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class frag_list extends Fragment {

    private TextView tvOut;
    private EditText etIn;
    private EditText etFile;
    private Button btn_w;
    private Button btn_r;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);
        final Context c = getContext();
        final Utils u = new Utils();
        final String id = u.genRandId();

        tvOut = (TextView) v.findViewById(R.id.tvOut);
        etIn = (EditText) v.findViewById(R.id.etIn);
        etFile = (EditText) v.findViewById(R.id.etFile);
        btn_w = (Button) v.findViewById(R.id.frag_list_write);
        btn_r = (Button) v.findViewById(R.id.frag_list_read);

        btn_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvOut.setText(u.read(c, id, etFile.getText().toString()));
            }
        });

        btn_w.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                u.save(c, id, etFile.getText().toString(), etIn.getText().toString());
            }
        });

        return v;
    }
}
