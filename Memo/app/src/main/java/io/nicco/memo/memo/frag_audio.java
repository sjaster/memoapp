package io.nicco.memo.memo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class frag_audio extends Fragment {

    TextView tv_lastrec;
    MediaRecorder recorder;
    Button btn_start, btn_stop, btn_pause;
    ImageView btn_save, btn_trash;
    Chronometer chrono;
    String fileName;
    EditText title;
    NoteAudio gta;

    long timeWhenStopped;
    boolean playing = false;
    boolean recording = false;
    boolean readyToSafe = false;

    /* PERMISSIONS */
    boolean granted = false;

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
        btn_start.setText(R.string.record_recording);
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
        btn_start.setText(R.string.record_start);
        btn_pause.setText(R.string.record_pause);

        readyToSafe = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_audio, container, false);

        if (!checkPermission())
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.RECORD_AUDIO}, Main.PERMISSION_REQUEST);

        title = (EditText) v.findViewById(R.id.frag_audio_title);
        chrono = (Chronometer) v.findViewById(R.id.frag_audio_chrono);
        tv_lastrec = (TextView) v.findViewById(R.id.frag_audio_tvlast);
        btn_start = (Button) v.findViewById(R.id.frag_audio_rec);
        btn_stop = (Button) v.findViewById(R.id.frag_audio_stop);
        btn_pause = (Button) v.findViewById(R.id.frag_audio_pause);
        btn_save = (ImageView) v.findViewById(R.id.frag_audio_save);
        btn_trash = (ImageView) v.findViewById(R.id.frag_audio_trash);

        gta = new NoteAudio(getContext());
        timeWhenStopped = 0;

        btn_trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("");
                new Utils().rm(getContext().getFilesDir() + "/record");
                gta.delete();
                gta = new NoteAudio(getContext());
                chrono.setVisibility(View.INVISIBLE);
                tv_lastrec.setVisibility(View.INVISIBLE);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!readyToSafe) {
                    Toast.makeText(getContext(), "No Recording", Toast.LENGTH_SHORT).show();
                    return;
                }
                gta.title = title.getText().toString();
                gta.save(new Utils().read(fileName));
                new Utils().rm(getContext().getFilesDir() + "/record");
                Toast.makeText(getContext(), "Record Saved!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission()) {
                    Toast.makeText(getContext(), "No Permissions", Toast.LENGTH_SHORT).show();
                    return;
                }
                chrono.setVisibility(View.VISIBLE);
                tv_lastrec.setVisibility(View.INVISIBLE);
                chrono.setBase(SystemClock.elapsedRealtime());
                chrono.start();
                startRecorder();
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!checkPermission())
                    return;
                Toast.makeText(getContext(), "For Storing click the Save Button", Toast.LENGTH_SHORT).show();
                tv_lastrec.setVisibility(View.VISIBLE);
                timeWhenStopped = 0;
                chrono.stop();
                stopRecorder();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            btn_pause.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                public void onClick(View v) {
                    toggle();
                }
            });
            btn_pause.setVisibility(View.VISIBLE);
        }

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private void toggle() {
        if (recorder == null)
            return;
        if (playing) {
            recorder.pause();
            timeWhenStopped = chrono.getBase() - SystemClock.elapsedRealtime();
            chrono.stop();
            btn_pause.setText(R.string.record_resume);
        } else {
            recorder.resume();
            chrono.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            chrono.start();
            btn_pause.setText(R.string.record_pause);
        }
        playing = !playing;
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }
}
