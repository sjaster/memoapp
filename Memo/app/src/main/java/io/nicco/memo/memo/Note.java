package io.nicco.memo.memo;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by nicco on 10/01/2017.
 */

public class Note {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_PHOTO = 1;
    public static final int TYPE_AUDIO = 2;
    public static final String MAIN_FILE = "main.json";

    public String title;
    public int datetime;
    public int type;
    public String id;
    public Boolean like;

    public Utils u = new Utils();
    public Context c;

    public Note(Context context, int t) {
        c = context;
        type = t;
        id = u.genRandId();
        updateTime();
    }

    public Note(Context context, int t, String newId) {
        c = context;
        type = t;
        id = newId;
        updateTime();
    }

    public void save() {
        try {
            updateTime();
            JSONObject main = new JSONObject();

            main.put("title", title);
            main.put("datetime", datetime);
            main.put("type", type);
            main.put("id", id);
            main.put("like", like);

            u.save(c, id, MAIN_FILE, main.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            String jsonString = u.read(c, id, MAIN_FILE);
            JSONObject main = new JSONObject(jsonString);

            title = main.getString("title");
            datetime = main.getInt("datetime");
            type = main.getInt("type");
            id = main.getString("id");
            like = main.getBoolean("like");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveExtra(String file, String msg) {
        u.save(c, id, file, msg);
    }

    public void updateTime() {
        this.datetime = (int) (new Date().getTime() / 1000);
    }

    public void delete() {
        u.rm(this.c.getFilesDir() + "/" + this.id);
    }

}
