package io.nicco.memo.memo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class Main extends FragmentActivity {

    public static String PACKAGE_NAME;

    public ArrayList<String> menu_names = new ArrayList<>();
    public ArrayList<LinearLayout> menu_items = new ArrayList<>();
    public ArrayList<ImageView> menu_icons = new ArrayList<>();

    FragmentPagerAdapter adapterViewPager;
    ViewPager vpPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PACKAGE_NAME = getApplicationContext().getPackageName();

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

            try {
                // Set onlick listener for the buttons
                curLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int cur = menu_items.indexOf(view);
                        vpPager.setCurrentItem(cur);
                        // setActiveMenuItem(cur);
                    }
                });
            } finally {

            }
        }

        vpPager = (ViewPager) findViewById(R.id.pager);
        adapterViewPager = new PagerAdapter(getSupportFragmentManager(), menu_names);
        vpPager.setAdapter(adapterViewPager);


    }

    public int toPixel(int dp) {
        DisplayMetrics metrics;
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return (dp * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;
    }

    public void setActiveMenuItem(int n) {
        int pxDefault = toPixel(R.dimen.ftr_icon_size);
        int pxActive = toPixel(R.dimen.ftr_icon_size_active);
        LinearLayout.LayoutParams defaultSize = new LinearLayout.LayoutParams(pxDefault, pxDefault);
        for (ImageView tmp : menu_icons) {
            tmp.setLayoutParams(defaultSize);
        }
        menu_icons.get(n).setLayoutParams(new LinearLayout.LayoutParams(pxActive, pxActive));
    }

    public static class PagerAdapter extends FragmentPagerAdapter {
        // https://guides.codepath.com/android/ViewPager-with-FragmentPagerAdapter
        private int NUM_ITEMS = 0;
        private ArrayList<String> menu = new ArrayList<>();

        public PagerAdapter(FragmentManager fragmentManager, ArrayList<String> m) {
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
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
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
