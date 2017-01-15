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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;

public class frag_audio extends Fragment {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    TextView tv_lastrec;
    String[] permissions = {Manifest.permission.RECORD_AUDIO};
    MediaRecorder recorder;
    Button btn_start, btn_stop, btn_pause;
    Chronometer chrono;
    String fileName;

    boolean playing = false;
    boolean recording = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "No permission to audio record", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
        }
    }

    protected void startRecorder() {
        if (recording)
            return;

        recorder = new MediaRecorder();

        fileName = getContext().getFilesDir().getAbsolutePath() + "/record_" + new Date().getTime() + ".aac";

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_audio, container, false);

        ActivityCompat.requestPermissions((Activity) getContext(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        chrono = (Chronometer) v.findViewById(R.id.frag_audio_chrono);
        tv_lastrec = (TextView) v.findViewById(R.id.frag_audio_tvlast);
        btn_start = (Button) v.findViewById(R.id.frag_audio_rec);
        btn_stop = (Button) v.findViewById(R.id.frag_audio_stop);
        btn_pause = (Button) v.findViewById(R.id.frag_audio_pause);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_lastrec.setVisibility(View.INVISIBLE);
                chrono.setBase(SystemClock.elapsedRealtime());
                chrono.start();
                startRecorder();
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tv_lastrec.setVisibility(View.VISIBLE);
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
        if (playing) {
            recorder.pause();
            chrono.stop();
            btn_pause.setText(R.string.record_resume);
        } else {
            recorder.resume();
            chrono.start();
            btn_pause.setText(R.string.record_pause);
        }
        playing = !playing;
    }

}
