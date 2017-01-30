package io.nicco.memo.memo;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class NoteAudio extends Note {

    static final String EXTRA_FILE = "audio.mp3";

    NoteAudio(Context c) {
        super(c, TYPE_AUDIO);
    }

    NoteAudio(Context c, String id) {
        super(c, id);
        loadExtra();
    }

    void saveExtra(byte[] b) {
        super.saveExtra(EXTRA_FILE, b);
        super.save();
    }

    void loadExtra() {
        super.load();
    }

    String getExtraFile() {
        return super.mk_path() + EXTRA_FILE;
    }

    void share() {
        File tmpPath = new File(c.getFilesDir(), "tmp");
        tmpPath.mkdir();
        tmpPath = new File(tmpPath.getPath(), title);

        try {
            InputStream in = new FileInputStream(new File(super.mk_path() + EXTRA_FILE));
            OutputStream out = new FileOutputStream(tmpPath);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("audio/mpeg");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(c, c.getPackageName(), tmpPath));
        c.startActivity(Intent.createChooser(sharingIntent, "Share..."));
    }
}
