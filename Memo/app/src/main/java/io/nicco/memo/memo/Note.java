package io.nicco.memo.memo;

import android.content.Context;

import java.util.Date;

/**
 * Created by nicco on 10/01/2017.
 */

public class Note {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_PHOTO = 1;
    public static final int TYPE_AUDIO = 2;

    public String title;
    public int datetime;
    public int type;
    public String id;

    private Utils u = new Utils();
    private Context c;

    public Note(Context c, int t) {
        this.c = c;
        this.type = t;
        this.id = u.genRandId();
        updateTime();
    }

    public void updateTime() {
        this.datetime = (int) (new Date().getTime() / 1000);
    }

    public void delete() {
        u.rm(this.c.getFilesDir() + "/" + this.id);
    }

}
