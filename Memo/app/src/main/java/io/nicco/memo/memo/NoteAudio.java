package io.nicco.memo.memo;

import android.content.Context;

class NoteAudio extends Note {

    static final String EXTRA_FILE = "audio.mp3";

    NoteAudio(Context c) {
        super(c, TYPE_AUDIO);
    }

    NoteAudio(Context c, String id) {
        super(c, id);
        load();
    }

    public void save(byte[] b) {
        super.saveExtra(EXTRA_FILE, b);
        super.save();
    }

    public void load() {
        super.load();
    }

    String getExtraFile() {
        return super.mk_path() + EXTRA_FILE;
    }
}
