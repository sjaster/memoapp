package io.nicco.memo.memo;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

class Note {
    static final int TYPE_TEXT = 0;
    static final int TYPE_PHOTO = 1;
    static final int TYPE_AUDIO = 2;
    static final String MAIN_FILE = "main.json";

    public String title;
    public int datetime;
    public int type;
    public String id;
    public Boolean like;

    Utils u = new Utils();
    Context c;

    Note(Context context, int t) {
        c = context;
        type = t;
        id = u.genRandId();
        updateTime();
    }

    Note(Context context, int t, String newId) {
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

            u.write(mk_path() + MAIN_FILE, main.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            String jsonString = new String(u.read(mk_path() + MAIN_FILE));
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

    void saveExtra(String file, byte[] b) {
        u.write(mk_path() + file, b);
    }

    byte[] loadExtra(String file) {
        return u.read(mk_path() + file);
    }

    public void updateTime() {
        this.datetime = (int) (new Date().getTime() / 1000);
    }

    public void delete() {
        u.rm(c.getFilesDir() + "/" + this.id);
    }

    private String mk_path() {
        return c.getFilesDir() + "/" + id + "/";
    }

}
