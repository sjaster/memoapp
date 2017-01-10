package io.nicco.memo.memo;

import android.content.Context;

/**
 * Created by nicco on 10/01/2017.
 */

public class NoteText extends Note {

    public NoteText(Context c) {
        super(c, TYPE_TEXT);
    }

    public void save (String msg){
        updateTime();
        u.save(c, id, "msg", msg);
    }
}
