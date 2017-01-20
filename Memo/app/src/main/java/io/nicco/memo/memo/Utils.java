package io.nicco.memo.memo;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

class Utils {

    String genRandId() {
        // TODO Check availabilty
        int length = 32;
        Random r = new Random();
        StringBuilder sb = new StringBuilder();

        while (length > 0) {
            sb.append(r.nextInt(9));
            length--;
        }

        return sb.toString();
    }

    void toast(Context c, String s) {
        Toast.makeText(c, s, Toast.LENGTH_SHORT).show();
    }

    void write(String file, byte[] msg) {
        try {
            String[] url = splitURL(file);
            File f = new File(url[0]);
            f.mkdirs();
            f = new File(url[0], url[1]);

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f.getAbsolutePath()));
            bos.write(msg);
            bos.flush();
            bos.close();
            Main.changed = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    byte[] read(String file) {
        File f = new File(file);
        byte[] ret = new byte[(int) f.length()];
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f.getAbsolutePath()));
            bis.read(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    void rm(String path) {
        try {
            rm_util(new File(path));
        } finally {
        }
    }

    private void rm_util(File f) {
        if (f.isDirectory())
            for (File child : f.listFiles())
                rm_util(child);
        f.delete();
    }

    private String[] splitURL(String url) {
        String[] split_url = new String[2];
        int pos = url.lastIndexOf("/");
        split_url[0] = url.substring(0, pos);
        split_url[1] = url.substring(pos + 1, url.length());
        return split_url;
    }
}
