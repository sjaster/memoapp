package io.nicco.memo.memo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Preview extends Activity {

    String id;
    Note n;
    NoteAudio na;
    NotePhoto np;
    NoteText nt;

    TextView tv_date;
    EditText et_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        id = getIntent().getStringExtra(EXTRA_MESSAGE);
        Log.i("NEW NOTE ID", id);
        n = new Note(this, id);

        tv_date = (TextView) findViewById(R.id.pv_time);
        et_title = (EditText) findViewById(R.id.pv_title);

        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

        et_title.setText(n.title);
        tv_date.setText(getResources().getText(R.string.pv_last_modified) + ": " + fmt.format(new Date((long) n.datetime * 1000)));

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

    void loadAudio() {

    }

    void loadPhoto() {

    }

    void loadText() {

    }

}
