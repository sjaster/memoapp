package io.nicco.memo.memo;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by nicco on 10/01/2017.
 */

public class Utils {

    public String genRandId() {
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

    public void write(Context c, String file, byte[] msg) {
        try {
            File f = new File(c.getFilesDir(), file);
            f.mkdirs();

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f.getAbsolutePath()));
            bos.write(msg);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] read(Context c, String file) {
        File f = new File(c.getFilesDir(), file);
        byte[] ret = new byte[(int) f.length()];
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f.getAbsolutePath()));
            //FileInputStream fis = new FileInputStream(f.getAbsolutePath());
            //fis.read(ret);
            bis.read(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("ReadBytes", new String(ret));

        return ret;
    }

    public void rm(String path) {
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
}
