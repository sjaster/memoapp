package io.nicco.memo.memo;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class Main extends FragmentActivity {

    public static String PACKAGE_NAME;

    public static ArrayList<String> menu_names = new ArrayList<>();
    public static ArrayList<LinearLayout> menu_items = new ArrayList<>();
    public static ArrayList<ImageView> menu_icons = new ArrayList<>();

    View indicator;
    int indicatorW;
    boolean measured = false;

    public static boolean changed = false;
    public final static int PERMISSION_REQUEST = 8888;

    FragmentPagerAdapter adapterViewPager;
    ViewPager vpPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Defining Names of the options in the bottom footer menu
        menu_names.add("list");
        menu_names.add("text");
        menu_names.add("photo");
        menu_names.add("audio");
        menu_names.add("settings");

        // For each name in the List -> icn_save the ImageView in menu_icons and the parent LinearLayout in menu_items
        for (int i = 0; i < menu_names.size(); ++i) {
            String cur = menu_names.get(i); // Get the current name
            LinearLayout curLayout = (LinearLayout) findViewById(getResources().getIdentifier("menu_" + cur, "id", getPackageName())); // Get the parent linear layout of the button
            ImageView curImg = (ImageView) findViewById(getResources().getIdentifier("ftr_img_" + cur, "id", getPackageName())); // Get the image view itself

            // Save both in the ArrayList
            menu_items.add(curLayout);
            menu_icons.add(curImg);

            Log.i("Cur", curLayout.toString());

            curLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vpPager.setCurrentItem(menu_items.indexOf(view));
                }
            });
        }

        indicator = findViewById(R.id.ftr_indicator);

        vpPager = (ViewPager) findViewById(R.id.pager);
        adapterViewPager = new PagerAdapter(getSupportFragmentManager(), menu_names);
        vpPager.setAdapter(adapterViewPager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            vpPager.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) indicator.getLayoutParams();
                    if (!measured) {
                        indicatorW = findViewById(R.id.ftr_indicator_w).getWidth() / menu_names.size();
                        params.width = indicatorW;
                        measured = true;
                    }
                    params.leftMargin = (int) (float) i / menu_names.size();
                    indicator.setLayoutParams(params);
                }
            });
        }
    }

    public static class PagerAdapter extends FragmentPagerAdapter {
        /*
        Code Adapted from:
        https://guides.codepath.com/android/ViewPager-with-FragmentPagerAdapter
         */
        private int NUM_ITEMS = 0;
        private ArrayList<String> menu = new ArrayList<>();

        PagerAdapter(FragmentManager fragmentManager, ArrayList<String> m) {
            super(fragmentManager);
            menu = m;
            NUM_ITEMS = menu.size();
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;

            try {
                Log.i("Getting:", PACKAGE_NAME + ".frag_" + menu.get(position));
                frag = (Fragment) (Class.forName(PACKAGE_NAME + ".frag_" + menu.get(position)).newInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return frag;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return menu.get(position);
        }

    }

}
