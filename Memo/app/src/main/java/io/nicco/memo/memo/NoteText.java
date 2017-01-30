package io.nicco.memo.memo;

import android.content.Context;
import android.content.Intent;

import static java.security.AccessController.getContext;

class NoteText extends Note {

    private static final String EXTRA_FILE = "body.txt";

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

    public void share() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, title+"\n"+text);
        c.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}
