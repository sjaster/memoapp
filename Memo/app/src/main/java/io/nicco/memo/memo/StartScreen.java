package io.nicco.memo.memo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Main.setActionBar(this);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), Main.class));
                    }
                },
                2000);

        setContentView(R.layout.activity_start_screen);
    }
}
