package io.nicco.memo.memo;

import android.content.Context;

/**
 * Created by nicco on 10/01/2017.
 */

public class NoteText extends Note {

    public String EXTRA_FILE = "body.json";
    public String text = "";

    public NoteText(Context c) {
        super(c, TYPE_TEXT);
    }

    public NoteText(Context c, String id) {
        super(c, TYPE_TEXT, id);
        load();
    }

    public void save() {
        super.saveExtra(EXTRA_FILE, text.getBytes());
        super.save();
    }

    public void load() {
        text = super.loadExtra(EXTRA_FILE).toString();
        super.load();
    }
}
