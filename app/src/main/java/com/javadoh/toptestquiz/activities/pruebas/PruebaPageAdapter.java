package com.javadoh.toptestquiz.activities.pruebas;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by luiseliberal on 05-07-2015.
 */

public class PruebaPageAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentosPag;

    public PruebaPageAdapter(FragmentManager fm, List<Fragment> fragmentosPag){
        super(fm);
        this.fragmentosPag = fragmentosPag;
    }

    @Override
    public int getCount() {
        return this.fragmentosPag.size();
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragmentosPag.get(position);
    }
}
