package io.nicco.memo.memo;

/**
 * Created by nicco on 10/01/2017.
 */

public class Note {
    private int NOTE_TYPE_TEXT = 0;
    private int NOTE_TYPE_PHOTO = 1;
    private int NOTE_TYPE_AUDIO = 2;

    public String title;
    public int datetime;
    public int type;
    public int id;

}
