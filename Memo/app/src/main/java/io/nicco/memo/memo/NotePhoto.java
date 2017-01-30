package io.nicco.memo.memo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
class NotePhoto extends Note {

    static final String EXTRA_FILE = "body.jpg";

    Image img;
    Bitmap bm;

    NotePhoto(Context c) {
        super(c, Note.TYPE_PHOTO);
    }

    NotePhoto(Context c, String id) {
        super(c, id);
        load();
    }

    public void save() {
        ByteBuffer buffer = img.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);

        super.saveExtra(EXTRA_FILE, bytes);
        super.save();
    }

    public void load() {
        byte[] img = super.loadExtra(EXTRA_FILE);
        try {
            bm = BitmapFactory.decodeByteArray(img, 0, img.length);
        } catch (Exception ignored) {

        }
        super.load();
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
        sharingIntent.setType("image/jpg");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(c, c.getPackageName(), tmpPath));
        c.startActivity(Intent.createChooser(sharingIntent, "Share..."));
    }
}
