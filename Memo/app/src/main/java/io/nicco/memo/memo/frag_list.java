package io.nicco.memo.memo;

import android.content.DialogInterface;
import android.graphics.Typeface;
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
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class frag_list extends Fragment {

    final String SORT_TYPE = "list_sort_type";
    final String SORT_ASC = "list_sort_asc";
    private final Handler handler = new Handler();
    private final int handlerUpdate = 100;
    Runnable runnable;
    ListView lv;
    TextView empty;
    ImageView btn_sort;
    ArrayList<Note> lvd;
    int sort_type = 0;
    boolean sort_asc = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);

        lv = (ListView) v.findViewById(R.id.frag_list_list);
        btn_sort = (ImageView) v.findViewById(R.id.frag_list_sort);
        empty = (TextView) v.findViewById(R.id.frag_list_empty);

        TextView title = (TextView) v.findViewById(R.id.frag_list_title);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Blanche-de-la-Fontaine.ttf");
        title.setTypeface(tf);

        btn_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                CharSequence items[] = new CharSequence[]{getResources().getString(R.string.list_sort_title), getResources().getString(R.string.list_sort_date)};
                builder.setSingleChoiceItems(items, sort_type, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int n) {
                        d.dismiss();
                        if (sort_type == n)
                            sort_asc = !sort_asc;
                        sort_type = n;
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
        super.onResume();

        loadState();
        load_list();
        setListener();
    }

    @Override
    public void onPause() {
        super.onPause();

        handler.removeCallbacks(runnable);
        saveState();
    }

    void saveState() {
        Utils u = new Utils();
        u.putPref(getContext(), SORT_ASC, String.valueOf(sort_asc));
        u.putPref(getContext(), SORT_TYPE, String.valueOf(sort_type));
    }

    void loadState() {
        Utils u = new Utils();
        try {
            sort_asc = Boolean.parseBoolean(u.getPref(getContext(), SORT_ASC));
            sort_type = Integer.parseInt(u.getPref(getContext(), SORT_TYPE));
        } catch (Exception ignored) {
            sort_asc = true;
            sort_type = 0;
        }
    }

    private void load_list() {
        lvd = read_notes();

        if (lvd.size() == 0) {
            empty.setVisibility(View.VISIBLE);
            return;
        } else {
            empty.setVisibility(View.GONE);
        }

        switch (sort_type) {
            case 0:
                Log.i("SORTING", "TITLE");
                Collections.sort(lvd, new ComparatorTitle());
                break;
            case 1:
                Log.i("SORTING", "Date");
                Collections.sort(lvd, new ComparatorTime());
                break;
        }
        if (!sort_asc)
            Collections.reverse(lvd);

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

    class ComparatorTitle implements Comparator<Note> {
        public int compare(Note left, Note right) {
            return left.title.compareToIgnoreCase(right.title);
        }
    }

    class ComparatorTime implements Comparator<Note> {
        public int compare(Note left, Note right) {
            if (left.datetime == right.datetime)
                return 0;
            else if (left.datetime > right.datetime)
                return 1;
            else
                return -1;
        }
    }
}
