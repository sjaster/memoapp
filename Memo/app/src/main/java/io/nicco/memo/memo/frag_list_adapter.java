package io.nicco.memo.memo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class frag_list_adapter extends BaseAdapter {

    private Context c;
    private ArrayList<Note> data;
    private static LayoutInflater inflater = null;
    private int[] type_img = {R.drawable.icn_file, R.drawable.icn_camera, R.drawable.icn_microphone};

    frag_list_adapter(Context context, ArrayList<Note> notes) {
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

        final Note n = data.get(position);

        TextView title = (TextView) vi.findViewById(R.id.frag_list_item_title);
        TextView subtitle = (TextView) vi.findViewById(R.id.frag_list_item_subtitle);
        ImageView type = (ImageView) vi.findViewById(R.id.frag_list_item_icon);
        ImageView share = (ImageView) vi.findViewById(R.id.frag_list_item_share);
        ImageView like = (ImageView) vi.findViewById(R.id.frag_list_item_like);
        LinearLayout ll = (LinearLayout) vi.findViewById(R.id.frag_list_item_root);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                n.preview();
            }
        });

        if (n.like)
            like.setBackgroundResource(R.drawable.icn_like_on);

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                n.toggleLike();
            }
        });

        title.setText(n.title);

        DateFormat fmt = new SimpleDateFormat("dd MMM yyyy");
        subtitle.setText(fmt.format(new Date((long) n.datetime * 1000)));

        type.setBackgroundResource(type_img[n.type]);

        return vi;
    }
}
