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

    public void save() {
        super.saveExtra(EXTRA_FILE, text);
        super.save();
    }

    public void load() {
    }
}
