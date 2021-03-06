package io.nicco.memo.memo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
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
    ImageView btn_back, btn_like, btn_share, btn_delete, btn_save;

    boolean touching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        id = getIntent().getStringExtra(EXTRA_MESSAGE);
        n = new Note(this, id);

        Main.setActionBar(this);

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
                n.delete();
                new Utils().toast(getApplicationContext(), "Deleted");
                Preview.super.onBackPressed();
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    loadPhoto();
                else
                    finish();
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
        findViewById(R.id.pv_cont_audio).setVisibility(View.VISIBLE);
        na = new NoteAudio(this, id);

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                na.share();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                na.title = et_title.getText().toString();
                na.save();
                new Utils().toast(getApplicationContext(), "Saved");
            }
        });

        final TextView cur_time, tot_time;
        final SeekBar seek;
        final ImageView pp;
        final MediaPlayer mp;

        cur_time = (TextView) findViewById(R.id.pv_cont_audio_cur_time);
        tot_time = (TextView) findViewById(R.id.pv_cont_audio_tot_time);
        seek = (SeekBar) findViewById(R.id.pv_cont_audio_seek);
        pp = (ImageView) findViewById(R.id.pv_cont_audio_play);

        mp = new MediaPlayer();
        try {
            mp.setDataSource(na.getExtraFile());
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            new Utils().toast(getApplicationContext(), "Error Getting Audio");
            finish();
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp.pause();
                mediaPlayer.seekTo(0);
                pp.setBackgroundResource(R.drawable.icn_rec_play);
            }
        });

        final int tot = mp.getDuration() / 1000;

        tot_time.setText(fmtTime(tot));
        pp.setBackgroundResource(R.drawable.icn_rec_play);

        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mp.isPlaying()) {
                    mp.pause();
                    pp.setBackgroundResource(R.drawable.icn_rec_play);
                } else {
                    mp.start();
                    pp.setBackgroundResource(R.drawable.icn_rec_pause);
                }
            }
        });


        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (touching)
                    mp.seekTo(i * mp.getDuration() / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                touching = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                touching = false;
            }
        });

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!touching) {
                    cur_time.setText(fmtTime(mp.getCurrentPosition() / 1000));
                    seek.setProgress((int) ((float) mp.getCurrentPosition()) / tot / 10);
                }
                handler.postDelayed(this, 100);
            }
        };
        handler.postDelayed(runnable, 0);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void loadPhoto() {
        np = new NotePhoto(this, id);

        findViewById(R.id.pv_cont_photo).setVisibility(View.VISIBLE);
        ImageView iv = (ImageView) findViewById(R.id.pv_cont_photo_img);
        iv.setImageBitmap(np.bm);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                np.title = et_title.getText().toString();
                np.save();
                new Utils().toast(getApplicationContext(), "Saved");
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                np.share();
            }
        });
    }

    void loadText() {
        nt = new NoteText(this, id);

        findViewById(R.id.pv_cont_text).setVisibility(View.VISIBLE);
        final EditText et_body = (EditText) findViewById(R.id.pv_cont_text_body);

        et_body.setText(nt.text);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nt.text = et_body.getText().toString();
                nt.title = et_title.getText().toString();
                nt.save();
                new Utils().toast(getApplicationContext(), "Saved");
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nt.text = et_body.getText().toString();
                nt.title = et_title.getText().toString();
                nt.save();
                nt.share();
            }
        });
    }

    String fmtTime(int t) {
        int min = t / 60;
        int sec = t - (min * 60);

        return min + ":" + sec;
    }

}
