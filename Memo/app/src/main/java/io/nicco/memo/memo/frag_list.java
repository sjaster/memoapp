package io.nicco.memo.memo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class frag_list extends Fragment {

    private final Handler handler = new Handler();
    private final int handlerUpdate = 100;
    Runnable runnable;

    ListView lv;
    ImageView btn_sort;
    ArrayList<Note> lvd;

    int sort_type = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);

        lv = (ListView) v.findViewById(R.id.frag_list_list);
        btn_sort = (ImageView) v.findViewById(R.id.frag_list_sort);

        btn_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LIST", "Sort btn");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                CharSequence items[] = new CharSequence[]{getResources().getString(R.string.list_sort_title), getResources().getString(R.string.list_sort_date)};
                builder.setSingleChoiceItems(items, sort_type, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int n) {
                        d.dismiss();
                        sort_type = n;
                        Log.i("DIALOG", String.valueOf(sort_type));
                        load_list();
                    }
                });
                builder.setTitle(R.string.list_sort);
                builder.create().show();
            }
        });

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
        lvd = read_notes();

        switch (sort_type) {
            case 0:
                sort_title(lvd, false, 0, lvd.size() - 1);
                break;
            case 1:
                sort_date(lvd, false, 0, lvd.size() - 1);
                break;
        }

        final frag_list_adapter adapter = new frag_list_adapter(getContext(), lvd);

        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lvd.get(i).preview();
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.toggle(i);
                return true;
            }
        });
    }

    ArrayList<Note> read_notes() {
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

    void sort_title(ArrayList<Note> o, boolean asc, int from, int to) {
        Log.i("SORT", "titile");
        if (from == to)
            return;
        Note tmp = o.get(from);
        for (int i = from; i <= to; i++) {
            if (tmp.title.compareToIgnoreCase(o.get(i).title) > 0)
                tmp = o.get(i);
        }
        Collections.swap(o, 0, o.indexOf(tmp));
        sort_title(o, asc, from + 1, to);
    }

    void sort_date(ArrayList<Note> o, boolean asc, int from, int to) {
        Log.i("SORT", "date");
        if (from == to)
            return;
        Note tmp = o.get(from);
        for (int i = from; i <= to; i++) {
            if (tmp.datetime < o.get(i).datetime)
                tmp = o.get(i);
        }
        Collections.swap(o, 0, o.indexOf(tmp));
        sort_date(o, asc, from + 1, to);
    }

    void setListener() {
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
        handler.postDelayed(runnable, 0);
    }
}
