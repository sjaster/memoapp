package io.nicco.memo.memo;

import android.content.Context;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
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
        load();
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                u.write(c, id + "/" + MAIN_FILE, main.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            String jsonString = u.read(c, id + "/" + MAIN_FILE).toString();
            JSONObject main = new JSONObject(jsonString);

            //Log.i("JSON String", jsonString);

            title = main.getString("title");
            datetime = main.getInt("datetime");
            type = main.getInt("type");
            id = main.getString("id");
            like = main.getBoolean("like");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveExtra(String file, byte[] b) {
        u.write(c, id + "" + file, b);
    }

    public byte[] loadExtra(String file) {
        return u.read(c, id + "" + file);
    }

    public void updateTime() {
        this.datetime = (int) (new Date().getTime() / 1000);
    }

    public void delete() {
        u.rm(this.c.getFilesDir() + "/" + this.id);
    }

}
