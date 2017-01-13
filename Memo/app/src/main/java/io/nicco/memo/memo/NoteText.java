package io.nicco.memo.memo;

import android.content.Context;

class NoteText extends Note {

    static final String EXTRA_FILE = "body.txt";

    public String text = "";

    NoteText(Context c) {
        super(c, TYPE_TEXT);
    }

    NoteText(Context c, String id) {
        super(c, id);
        load();
    }

    public void save() {
        super.saveExtra(EXTRA_FILE, text.getBytes());
        super.save();
    }

    public void load() {
        text = new String(super.loadExtra(EXTRA_FILE));
        super.load();
    }
}
