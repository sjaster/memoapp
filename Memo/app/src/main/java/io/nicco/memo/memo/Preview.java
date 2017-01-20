package io.nicco.memo.memo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Preview extends Activity {

    String id;
    Note n;
    NoteAudio na;
    NotePhoto np;
    NoteText nt;

    TextView tv_date;
    EditText et_title;
    ImageView btn_back, btn_like, btn_share, btn_delete, btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        id = getIntent().getStringExtra(EXTRA_MESSAGE);
        Log.i("NEW NOTE ID", id);
        n = new Note(this, id);

        tv_date = (TextView) findViewById(R.id.pv_time);
        et_title = (EditText) findViewById(R.id.pv_title);
        btn_back = (ImageView) findViewById(R.id.pv_back);
        btn_like = (ImageView) findViewById(R.id.pv_like);
        btn_share = (ImageView) findViewById(R.id.pv_share);
        btn_delete = (ImageView) findViewById(R.id.pv_trash);
        btn_save = (ImageView) findViewById(R.id.pv_save);

        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        et_title.setText(n.title);
        tv_date.setText(getResources().getText(R.string.pv_last_modified) + ": " + fmt.format(new Date((long) n.datetime * 1000)));

        setLike();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preview.super.onBackPressed();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "DELETE");
                AlertDialog.Builder builder = new AlertDialog.Builder(Preview.this);
                builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        n.delete();
                        Preview.super.onBackPressed();
                    }
                });
                builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.create();
            }
        });

        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                n.toggleLike();
                setLike();
            }
        });

        switch (n.type) {
            case Note.TYPE_AUDIO:
                loadAudio();
                break;
            case Note.TYPE_PHOTO:
                loadPhoto();
                break;
            case Note.TYPE_TEXT:
                loadText();
                break;
        }
    }

    void setLike() {
        int img = R.drawable.icn_like_off;
        if (n.like)
            img = R.drawable.icn_like_on;
        btn_like.setBackgroundResource(img);
    }

    void loadAudio() {
        na = new NoteAudio(this, id);
    }

    void loadPhoto() {
        np = new NotePhoto(this, id);
    }

    void loadText() {
        nt = new NoteText(this, id);
    }

}
