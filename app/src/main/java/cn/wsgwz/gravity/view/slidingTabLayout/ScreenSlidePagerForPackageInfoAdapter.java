/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package cn.wsgwz.gravity.view.slidingTabLayout;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import cn.wsgwz.gravity.fragment.PackageIfoFragment;

/**
* Created by arne on 18.11.14.
*/
public class ScreenSlidePagerForPackageInfoAdapter extends FragmentStatePagerAdapter {
    private List<PackageIfoFragment>  packageIfoFragmentList = new ArrayList<>();
    private List<String> titleStringList = new ArrayList<>();
    public ScreenSlidePagerForPackageInfoAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        return  packageIfoFragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleStringList.get(position);
    }
    @Override
    public int getCount() {
        return packageIfoFragmentList.size();
    }
    public void addTab(String titltStr,PackageIfoFragment packageIfoFragment){
        packageIfoFragmentList.add(packageIfoFragment);
        titleStringList.add(titltStr);
    }

}
