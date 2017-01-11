package io.nicco.memo.memo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class frag_list extends Fragment {

    private ListView lv;
    private NoteText nt;
    SimpleCursorAdapter cursorAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);
        final Context c = getContext();
        final Utils u = new Utils();
        final String id = u.genRandId();
        nt = new NoteText(getContext());
        lv = (ListView) v.findViewById(R.id.frag_list_list);

        ArrayList<String> folders = new ArrayList<>();
        File f = new File(String.valueOf(getContext().getFilesDir()));
        File[] files = f.listFiles();
        for (File inFile : files) {
            if (inFile.isDirectory() && inFile.length()==32) {
                folders.add(inFile.getName());
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String> (getContext(),android.R.layout.simple_list_item_1,folders);

        lv.setAdapter(arrayAdapter);

        return v;
    }
}
