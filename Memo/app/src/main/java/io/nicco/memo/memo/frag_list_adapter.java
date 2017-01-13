package io.nicco.memo.memo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nicco on 13/01/2017.
 */

public class frag_list_adapter extends BaseAdapter {

    private Context c;
    private ArrayList<Note> data;
    private static LayoutInflater inflater = null;

    public frag_list_adapter(Context context, ArrayList<Note> notes) {
        c = context;
        data = notes;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.fragment_list_item, null);

        Note n = data.get(position);

        TextView title = (TextView) vi.findViewById(R.id.frag_list_item_title);
        ImageView type = (ImageView) vi.findViewById(R.id.frag_list_item_icon);
        ImageView share = (ImageView) vi.findViewById(R.id.frag_list_item_share);
        ImageView like = (ImageView) vi.findViewById(R.id.frag_list_item_like);

        if (n.like)
            like.setBackgroundResource(R.drawable.icn_like_on);


        title.setText(n.title);

        return vi;
    }
}