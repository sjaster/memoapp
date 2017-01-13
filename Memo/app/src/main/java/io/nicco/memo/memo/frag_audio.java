package io.nicco.memo.memo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.os.EnvironmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class frag_audio extends Fragment {

    private int i;
    private String mFileName;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private Context c;
    private MediaRecorder recorder;
    private Button btn_start, btn_stop, btn_pause;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        // if (!permissionToRecordAccepted ) finish();
    }

    protected void startRecorder() {

        i++;
        recorder = new MediaRecorder();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH-mm-ss");
        Date now = new Date();

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/record_"+formatter.format(now)+".3gp";

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncodingBitRate(16);
        recorder.setAudioSamplingRate(44100);
        recorder.setOutputFile(mFileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");

        }

        recorder.start();
    }

    private void stopRecorder(){
        recorder.stop();
        recorder.release();
        recorder = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_audio, container, false);

        ActivityCompat.requestPermissions((Activity) getContext(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        btn_start = (Button) v.findViewById(R.id.frag_audio_rec);
        btn_stop = (Button) v.findViewById(R.id.frag_audio_stop);
        btn_pause = (Button) v.findViewById(R.id.frag_audio_pause);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecorder();
            }
        });

      /*  btn_pause.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                recorder.pause();
            }
        });*/

        btn_stop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                stopRecorder();
            }
        });
        return v;
    }
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

}
