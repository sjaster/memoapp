package io.nicco.memo.memo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.nio.ByteBuffer;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
class NotePhoto extends Note {

    static final String EXTRA_FILE = "body.jpg";

    public Image img;
    public Bitmap bm;

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
}
