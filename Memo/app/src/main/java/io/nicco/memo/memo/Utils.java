package io.nicco.memo.memo;

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

    void write(String file, byte[] msg) {
        try {
            File f = new File(file);
            f.mkdirs();

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f.getAbsolutePath()));
            bos.write(msg);
            bos.flush();
            bos.close();
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
}
