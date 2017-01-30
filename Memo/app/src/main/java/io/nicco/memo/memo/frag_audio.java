package io.nicco.memo.memo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class frag_audio extends Fragment {

    TextView tv_lastrec;
    MediaRecorder recorder;
    ImageView btn_main, btn_pause, btn_save, btn_trash;
    View visualizer, visualizerWE;
    Chronometer chrono;
    String fileName;
    EditText title;
    NoteAudio na;
    int MAX_RANGE = 32767;
    Runnable runnable;
    long timeWhenStopped;
    boolean playing = false;
    boolean recording = false;
    boolean readyToSafe = false;
    /* PERMISSIONS */
    boolean granted = false;
    private Handler handler;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Main.PERMISSION_REQUEST:
                granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
    }

    protected void startRecorder() {
        if (recording)
            return;

        if (!checkPermission()) {
            new Utils().toast(getContext(), "No Permissions");
            return;
        }

        recorder = new MediaRecorder();

        fileName = getContext().getFilesDir().getAbsolutePath() + "/record";
        new File(fileName).mkdirs();

        fileName += "/" + new Date().getTime() + ".mp3";

        recorder.setOutputFile(fileName);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        // MP3 ACC 44.1kHz 320kbps
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncodingBitRate(320000);
        recorder.setAudioSamplingRate(44100);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder.start();
        recording = true;
        playing = true;
        setVisualizer();

        btn_main.setBackgroundResource(R.drawable.icn_rec_stop);
        chrono.setVisibility(View.VISIBLE);
        tv_lastrec.setVisibility(View.INVISIBLE);
        chrono.setBase(SystemClock.elapsedRealtime());
        btn_pause.setBackgroundResource(R.drawable.icn_rec_pause);
        chrono.start();
    }

    private void stopRecorder() {

        if (!recording)
            return;

        recording = false;
        playing = false;
        if (recorder != null) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }
        readyToSafe = true;
        btn_main.setBackgroundResource(R.drawable.icn_rec_mic);
        tv_lastrec.setVisibility(View.VISIBLE);
        timeWhenStopped = 0;
        chrono.stop();
        na.title = title.getText().toString();
        btn_pause.setBackgroundResource(R.drawable.icn_rec_pause);
        na.saveExtra(new Utils().read(fileName));
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private void playPause() {
        if (recorder == null)
            return;
        if (playing) {
            recorder.pause();
            timeWhenStopped = chrono.getBase() - SystemClock.elapsedRealtime();
            chrono.stop();
            btn_pause.setBackgroundResource(R.drawable.icn_rec_play);
        } else {
            recorder.resume();
            chrono.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            chrono.start();
            btn_pause.setBackgroundResource(R.drawable.icn_rec_pause);
        }
        playing = !playing;
    }

    void toggleRecorder() {
        if (recording)
            stopRecorder();
        else
            startRecorder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_audio, container, false);

        title = (EditText) v.findViewById(R.id.frag_audio_title);
        chrono = (Chronometer) v.findViewById(R.id.frag_audio_chrono);
        tv_lastrec = (TextView) v.findViewById(R.id.frag_audio_tvlast);
        btn_main = (ImageView) v.findViewById(R.id.frag_audio_btn_main);
        btn_pause = (ImageView) v.findViewById(R.id.frag_audio_btn_pause);
        btn_save = (ImageView) v.findViewById(R.id.frag_audio_save);
        btn_trash = (ImageView) v.findViewById(R.id.frag_audio_trash);

        btn_main.setBackgroundResource(R.drawable.icn_rec_mic);
        btn_pause.setBackgroundResource(R.drawable.icn_rec_pause);

        visualizerWE = v.findViewById(R.id.frag_audio_visualizer_measure);
        visualizer = v.findViewById(R.id.frag_audio_visualizer);

        na = new NoteAudio(getContext());
        timeWhenStopped = 0;

        btn_trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("");
                na.delete();
                na = new NoteAudio(getContext());
                chrono.setVisibility(View.INVISIBLE);
                tv_lastrec.setVisibility(View.INVISIBLE);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!readyToSafe) {
                    new Utils().toast(getContext(), "No Recording");
                    return;
                }
                na.title = title.getText().toString();
                na.saveExtra(new Utils().read(fileName));
                new Utils().toast(getContext(), "Recording Saved");
            }
        });

        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecorder();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            btn_pause.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                public void onClick(View v) {
                    playPause();
                }
            });
            btn_pause.setVisibility(View.VISIBLE);
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recording)
            stopRecorder();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        new Utils().rm(getContext().getFilesDir() + "/record");
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void setVisualizer() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                int cur;
                if (recorder != null)
                    cur = (int) ((double) recorder.getMaxAmplitude() / MAX_RANGE * visualizerWE.getWidth());
                else
                    cur = 0;
                visualizer.setLayoutParams(new LinearLayout.LayoutParams(cur, visualizer.getHeight()));
                handler.postDelayed(this, 50);
            }
        };
        handler.postDelayed(runnable, 50);
    }
}
