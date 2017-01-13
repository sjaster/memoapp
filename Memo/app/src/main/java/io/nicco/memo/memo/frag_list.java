package io.nicco.memo.memo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class frag_list extends Fragment {

    private ListView lv;
    private NoteText nt;
    private final Handler handler = new Handler();
    Runnable runnable;
    private final int handlerUpdate = 100;

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
        setListener();
        super.onResume();
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    private void load_list() {
        frag_list_adapter adapter = new frag_list_adapter(getContext(), read_notes());
        lv.setAdapter(adapter);
    }

    private ArrayList<Note> read_notes() {
        ArrayList<Note> notes = new ArrayList<>();
        File f = new File(String.valueOf(getContext().getFilesDir()));
        File[] files = f.listFiles();
        for (File inFile : files) {
            if (inFile.isDirectory() && inFile.getName().length() == 32) {
                String cur = inFile.getName();
                notes.add(new Note(getContext(), cur));
            }
        }
        return notes;
    }

    private void setListener() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (Main.changed) {
                    load_list();
                    Main.changed = false;
                }
                handler.postDelayed(this, handlerUpdate);
            }
        };
        handler.postDelayed(runnable, handlerUpdate);
    }
}
