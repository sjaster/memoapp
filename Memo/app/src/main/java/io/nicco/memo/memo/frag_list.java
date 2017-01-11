package io.nicco.memo.memo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class frag_list extends Fragment {

    private ListView lv;
    private NoteText nt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);

        nt = new NoteText(getContext());
        lv = (ListView) v.findViewById(R.id.frag_list_list);

        return v;
    }

    @Override
    public void onResume() {
        load_list();
        super.onResume();
    }

    private void load_list() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, read_notes());
        lv.setAdapter(arrayAdapter);
    }

    private ArrayList<String> read_notes() {
        ArrayList<String> notes = new ArrayList<>();
        File f = new File(String.valueOf(getContext().getFilesDir()));
        File[] files = f.listFiles();
        for (File inFile : files) {
            if (inFile.isDirectory() && inFile.getName().length() == 32) {
                Log.i("Path", inFile.getAbsolutePath());
                String cur = inFile.getName();
                NoteText nt = new NoteText(getContext(), cur);
                //Log.i("Title", nt.toString());
                notes.add(inFile.getName());
            }
        }
        return notes;
    }
}
