package io.nicco.memo.memo;

import android.content.Context;
import android.media.Image;

import java.nio.ByteBuffer;

class NotePhoto extends Note {

    static final String EXTRA_FILE = "body.jpg";

    public Image img;

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
        //text = new String(super.loadExtra(EXTRA_FILE));
        super.load();
    }
}
