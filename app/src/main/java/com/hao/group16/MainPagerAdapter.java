package com.hao.group16;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;


public class MainPagerAdapter extends FragmentPagerAdapter {

    private final int PAGER_COUNT = 4;
    private NewsListFragment myFragment1 = null;
    private BookmarkFragment myFragment2 = null;
    private MyNewsFragment myFragment3 = null;
    private UserFragment myFragment4 = null;


    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        myFragment1 = new NewsListFragment();
        myFragment2 = new BookmarkFragment();
        myFragment3 = new MyNewsFragment();
        myFragment4 = new UserFragment();
    }


    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //System.out.println("position destroy" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case MainActivity.PAGE_ONE:
                fragment = myFragment1;
                break;
            case MainActivity.PAGE_TWO:
                fragment = myFragment2;
                break;
            case MainActivity.PAGE_THREE:
                fragment = myFragment3;
                break;
            case MainActivity.PAGE_FOUR:
                fragment = myFragment4;
                break;
        }
        return fragment;
    }


}

