package io.nicco.memo.memo;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Created by nicco on 10/01/2017.
 */

public class Utils {

    public String genRandId() {
        int length = 32;
        Random r = new Random();
        StringBuilder sb = new StringBuilder();

        while (length > 0) {
            sb.append(r.nextInt(9));
            length--;
        }

        return sb.toString();
    }

    public void save(Context c, String id, String file, String msg) {
        File dir = new File(c.getFilesDir(), id);
        dir.mkdirs();
        File f = new File(dir, file);

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(f.getAbsoluteFile());
            Log.i("File", String.valueOf(f.getAbsoluteFile()));
            bw = new BufferedWriter(fw);
            bw.write(msg);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public String read(Context c, String id, String file) {
        StringBuilder sb = new StringBuilder();
        try {
            File[] files = new File(c.getFilesDir().toString() + "/" + id).listFiles();
            for (File aFile : files) {
                sb.append(aFile.toString());
                sb.append("\n");
            }
        } finally {
            
        }

        return sb.toString();
    }
}
